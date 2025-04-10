package com.fsyy.netifacecardtraffic.monitor;

import com.fsyy.netifacecardtraffic.utils.ShellExecutor;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FileMonitor {
    /**
     * 网卡数据处理中心-数据推送接口地址
     */
    private static String DATA_PUSH_URL;

    private static Map<String, String> cardAndIp = new ConcurrentHashMap<>();

    private static ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void setDataPushUrl(String dataPushUrl){
        DATA_PUSH_URL = dataPushUrl;
    }

    /**
     * 初始化文件监听器
     */
    public static void init(){
        try{
            // 初始化网卡与IP
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while(nifs.hasMoreElements()){
                NetworkInterface nif = nifs.nextElement();
                // 获得与该网络接口绑定的IP地址，一般只有一个
                Enumeration<InetAddress> addresses = nif.getInetAddresses();
                while(addresses.hasMoreElements()){
                    InetAddress addr = addresses.nextElement();
                    if(addr instanceof Inet4Address) {  // 只关心IPv4地址
                        log.info("### 网卡：{}_{}", addr.getHostAddress(), nif.getName());
                        if(StringUtils.isNoneBlank(nif.getName(), addr.getHostAddress())){
                            cardAndIp.put(nif.getName(), addr.getHostAddress());
                        }
                    }
                }
            }

            log.info("开始初始化文件监听器");
            File file = new File("/data/");
            FileAlterationObserver observer = new FileAlterationObserver(file.getCanonicalFile(), FileFilterUtils.suffixFileFilter(".txt"));
            observer.addListener(new FileAlterationListenerAdaptor(){
                @Override
                public void onFileChange(File file) {
                    log.info("***** {} Changed.", file.getName());
                    String filePath = file.getPath();

                    try(Stream<String> lines = Files.lines(Paths.get(filePath))){
                        long count = lines.count(); // 总行数
                        if(count > 0){
                            try(Stream<String> lines2 = Files.lines(Paths.get(filePath))){
                                // 输出最新1行
                                List<String> new2 = lines2.skip(count - 1).limit(1).collect(Collectors.toList());
                                if(!new2.isEmpty()){
                                    parseLine(new2.get(0));
                                }
                            }
                        }
                    }catch (IOException e){
                        log.error(e.getMessage(), e.getCause());
                    }
                }
            });
            // 周期5s
            long interval = TimeUnit.SECONDS.toMillis(5);
            new FileAlterationMonitor(interval, observer).start();
        }catch (Exception e){
            log.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * 解析单条数据
     * @param line
     */
    private static void parseLine(String line){
        // yyyy-MM-dd HH:ss -> eth0: 100000 200000 0 0 0 0 0 0 300000 400000 0 0 0 0 0 0
        log.info("{}", line);
        String card = StringUtils.trimToEmpty(StringUtils.substringBetween(line, "->", ":"));
        String date = StringUtils.trimToEmpty(StringUtils.substringBeforeLast(line, "->"));
        String[] t = StringUtils.substringAfterLast(line, ": ").split(" ");
        Long rx = NumberUtils.toLong(t[0]);
        Long tx = NumberUtils.toLong(t[8]);
        log.info(" data1: {}, {}, {}, {}", card, date, rx, tx);
        // 追加cardIP
        card = String.format("%s_%s", cardAndIp.get(card), card);
        // 推送数据
        touchSSE(card, date, rx, tx);
    }

    /**
     * 触发SSE接口发送数据
     * @param card
     * @param date
     * @param rx
     * @param tx
     */
    private static void touchSSE(String card, String date, Long rx, Long tx){
        try{
            // 推送地址优先级：1.命令行指定 2.docker-compose环境变量注入 3.默认值
            if(StringUtils.isBlank(DATA_PUSH_URL)){
                DATA_PUSH_URL = StringUtils.defaultIfBlank(System.getenv("DATA_PUSH_URL"), "http://xxxx:8081/sse/touch");
            }
            String commands = Unirest.post(DATA_PUSH_URL)
                    .field("model", "packets")  // 发送的是原始的packets byte数
                    .field("card", card)
                    .field("date", date)
                    .field("rx", rx)
                    .field("tx", tx)
                    .asString()
                    .getBody();
            if(StringUtils.isNotBlank(commands)){
                log.info("#### {} will execute wondershape: {}", card, commands);
                executeWonderShaper(card, commands);
            }
        }catch (UnirestException e){
            log.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * 执行WonderShaper命令
     * @param card
     * @param commands
     */
    private static void executeWonderShaper(String card, String commands){
        // 线程池异步执行
        executorService.execute(() -> {
            try{
                log.info("----------- wondershaper command ------------");
                for(String command : commands.split(";")){
                    // 全路径命令
                    command = command.replace("wondershaper", ShellExecutor.wondershaper);
                    log.info("{}", command);

                    // 不可用stream并行执行
                    ShellExecutor.execute(command);
                }
                // 执行命令成功后回调
                log.info("callback for {}", card);
                Unirest.post(DATA_PUSH_URL.replace("/sse/touch", "api/limit/callback")).field("card", card).asString();
            }catch (IOException | UnirestException e){
                log.error(e.getMessage(), e.getCause());
            }
        });
    }
}
