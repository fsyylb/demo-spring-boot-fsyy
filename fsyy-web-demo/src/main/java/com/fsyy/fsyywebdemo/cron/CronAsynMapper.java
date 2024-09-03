package com.fsyy.fsyywebdemo.cron;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface CronAsynMapper {
    @Insert("insert into fsyy_cron_asyn(asyn_type, asyn_version) values (#{asynType}, 1)")
    int init(@Param("asynType") String asynType);

    @Select("select count(*) from fsyy_cron_asyn where asyn_type = #{asynType}")
    int total(@Param("asynType") String asynType);

    @Update("update fsyy_cron_asyn set asyn_version = asyn_version + 1 where asyn_type = #{asynType} and asyn_version = #{asynVersion}")
    int updateVersion(@Param("asynType") String asynType, @Param("asynVersion") long asynVersion);

    @Select("select asyn_version from fsyy_cron_asyn where asyn_type = #{asynType}")
    long queryVersion(@Param("asynType") String asynType);
}
