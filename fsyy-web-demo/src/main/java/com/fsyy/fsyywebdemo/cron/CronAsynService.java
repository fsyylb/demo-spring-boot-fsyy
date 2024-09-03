package com.fsyy.fsyywebdemo.cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

//@Component
@Slf4j
public class CronAsynService implements InitializingBean {
    //@Autowired
    private CronAsynMapper cronAsynMapper;

    private static final String ASYN_TYPE = "app_info_cron";

    private long version;

    @Override
    public void afterPropertiesSet() throws Exception {
        int total = cronAsynMapper.total(ASYN_TYPE);
        log.info("total value is : " + total);
        if(total == 0){
            cronAsynMapper.init(ASYN_TYPE);
        }
        version = cronAsynMapper.queryVersion(ASYN_TYPE);
    }

    public boolean updateVersion(){
        int result = cronAsynMapper.updateVersion(ASYN_TYPE, version);
        if(result == 0){
            return false;
        }
        version = cronAsynMapper.queryVersion(ASYN_TYPE);
        return true;
    }

    public void asynVersion(){
        version = cronAsynMapper.queryVersion(ASYN_TYPE);
    }

    public long getVersion(){
        return version;
    }

    public void setVersion(long version){
        this.version = version;
    }
}
