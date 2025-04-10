package com.fsyy.netifacecardtraffic;

import com.fsyy.netifacecardtraffic.monitor.FileMonitor;
import com.fsyy.netifacecardtraffic.utils.ShellExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// @SpringBootApplication
@Slf4j
public class NetIfaceCardTrafficApplication {

    /*public static void main(String[] args) {
        SpringApplication.run(NetIfaceCardTrafficApplication.class, args);
    }*/

    /**
     * 线程池保证程序一直运行
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        log.info("###################################################################################################################");
        log.info("please run:\t java -jar net-iface-card-traffic-jar-with-dependencies.jar --data_push_url=http://xxxx:8081/sse/touch");
        log.info("###################################################################################################################");
        if(args.length > 0){
            String url = Stream.of(args).filter(arg -> arg.contains("--data_push_url=")).map(arg -> StringUtils.substringAfter(arg, "="))
                    .collect(Collectors.joining());
            if(StringUtils.isNotBlank(url)){
                log.info("data_push_url={}", url);
                FileMonitor.setDataPushUrl(url);
            }
        }

        // 检查OS、物理网卡
        log.info("------ 开始检查OS、物理网卡");
        if(!SystemUtils.IS_OS_UNIX){
            log.error("不支持的OS，程序终止");
            return;
        }
        List<String> allCards = ShellExecutor.execute("ls /sys/class/net/");
        List<String> virCards = ShellExecutor.execute("ls /sys/devices/virtual/net/");
        List<String> netCards = allCards.stream().filter(x -> !virCards.contains(x)).collect(Collectors.toList());
        if(netCards.isEmpty()){
            log.error("未发现任何物理网卡，请检查网络配置！");
            return;
        }
        log.info("###### OS、物理网卡检查通过 ######");

        // 打印物理网卡名
        log.info("----- 发现物理网卡 -----");
        netCards.stream().forEach(log::info);

        // 拷贝wondershaper1.4.1
        copyWondershaper();

        // 处理网卡流量数据
        new File("/data/").mkdirs();
        long initialDelay = 60000L - System.currentTimeMillis() % 60000L - 1; // 计算延迟（微调1ms），保证准时启动
        log.info("initialDelay {} Seconds", initialDelay / 1000.0);
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            try{
                printInfo(netCards);
            }catch (IOException e){
                log.error(e.getMessage(), e);
            }
        }, initialDelay, 60000L, TimeUnit.MICROSECONDS);

        // 启动文件监听
        FileMonitor.init();
    }

    /**
     * jar运行时从内部拷贝wondershaper1.4.1文件到当前目录
     */
    private static void copyWondershaper(){
        String protocol = NetIfaceCardTrafficApplication.class.getResource("").getProtocol();
        log.info("------ protocol: {}", protocol);
        if("jar".equals(protocol)){
            // resources下需要有/wondershaper1.4.1
            try(InputStream is = NetIfaceCardTrafficApplication.class.getResourceAsStream("/wondershaper1.4.1/wondershaper")){
                File file = new File("wondershaper");
                FileUtils.copyInputStreamToFile(is, file);
                String cmdPath = file.getCanonicalPath();
                ShellExecutor.execute("chmod +x " + cmdPath);  // 赋可执行权限
                log.info("############# wondershaper path: {}", cmdPath);
            }catch (IOException e){
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 过滤物理网卡流量信息
     * @param net
     * @throws IOException
     */
    private static void printInfo(List<String> net) throws IOException {
        String time = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");

        // 解决StringUtils.containsAny bug, 如：eth0、veth0ef5c34
        List<String> cards = net.stream().map(n -> n + ":").collect(Collectors.toList());
        ShellExecutor.execute("cat /proc/net/dev")
                .stream()
                .filter(line -> StringUtils.containsAny(line, cards.toArray(new String[0]))) // 过滤物理网卡
                .map(line -> StringUtils.replaceAll(line, " +", " ")) // 去除多余空格
                .forEach(line -> writeToDayFile(String.format("%s -> %s", time, line)));
    }

    /**
     * 追加写入文件/data/${date}-${card}.txt
     * @param content
     */
    private static void writeToDayFile(String content){
        try{
            // yyyy-MM-dd HH:ss -> eth0 10000 20000 0 0 0 0 0 0 11111 22222 0 0 0 0 0 0
            log.info("{}", content);
            String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");
            String card = StringUtils.trimToEmpty(StringUtils.substringBetween(content, "->", ":"));
            File file = new File(String.format("/data/%s-%s.txt", date, card));
            try(FileWriter writer = new FileWriter(file, true)){ // 追加
                writer.write(content);
                writer.write(System.lineSeparator()); // 换行
                writer.flush();
            }
        }catch (IOException e){
            log.error(e.getMessage(), e);
        }
    }
}
