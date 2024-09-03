package com.fsyy.fsyywebdemo.cron;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

//@EnableScheduling
//@Component
@Slf4j
public class ScheduledTasks {
    //@Autowired
    private CronAsynService cronAsynService;

    private static final SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm:ss");

    //@Scheduled(fixedRate = 60 * 1000) // 1分钟
    //@Scheduled(cron = "30 0/1 * * * ?") // 从30秒开始，每1分钟更新
    //@Transactional
    public void batchUpdate(){
        log.info("asynService before update version's value is : " + cronAsynService.getVersion());
        boolean updateResult = cronAsynService.updateVersion();
        if(!updateResult){
            cronAsynService.asynVersion();
            log.info("现在时间： " + dataFormat.format(new Date()) + "; 同步失败； 同步失败更新后的version's value is : " + cronAsynService.getVersion());
            return;
        }
        //doSomething
        log.info("现在时间 ： " + dataFormat.format(new Date()));
    }
}
