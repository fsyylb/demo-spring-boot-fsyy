package com.fsyy.fsyywebdemo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 日志保存数据源
 */
public class LogPoolManager {
    private static DataSource dataSource;

    // 外部配置文件位置
    private static String configLocation;

    private LogPoolManager(){
        super();
    }

    public static void setConfigLocation(String configLocation){
        LogPoolManager.configLocation = configLocation;
    }

    public static synchronized void init(){
        try{
            // 日志初始化先于springboot，生产环境启动方式为 java -jar xxx.jar --spring.config.location=application-prod.yml
            if(StringUtils.isNotBlank(configLocation)){
                File file = new File(configLocation);
                String text = FileUtils.readFileToString(file, StandardCharsets.UTF_8.toString());
                Properties props = YamlToPropertiesUtils.yamlToProperties(text);
                System.out.println("---------spring.config.location 初始化日志数据库URL：" + props.getProperty("spring.datasource.url"));
                dataSource = DataSourceBuilder.create()
                        .type(DruidDataSource.class)
                        .url(props.getProperty("spring.datasource.url"))
                        .username(props.getProperty("spring.datasource.username"))
                        .password(props.getProperty("spring.datasource.password"))
                        .driverClassName(props.getProperty("spring.datasource.driver-class-name"))
                        .build();
            }else{
                ResourceBundle config = ResourceBundle.getBundle("jdbc");
                System.out.println("---------- jdbc 初始化日志数据库URL：" + config.getString("jdbc.url"));
                dataSource = DataSourceBuilder.create()
                        .type(DruidDataSource.class)
                        .url(config.getString("jdbc.url"))
                        .username(config.getString("jdbc.username"))
                        .password(config.getString("jdbc.password"))
                        .driverClassName(config.getString("jdbc.driver"))
                        .build();
            }
            initLogDataBase();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化日志数据库
     */
    private static void initLogDataBase(){
        try{
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            String path = "schema.sql";
            Resource resource = new ClassPathResource(path);
            String sqlText = IOUtils.toString(resource.getInputStream(), "utf-8");
            Arrays.stream(sqlText.split(";"))
                    .filter(s -> StringUtils.isNotBlank(s))
                    .forEach(sql -> {
                        try{
                            jdbcTemplate.execute(sql);
                        }catch (DataAccessException e){
                            // e.printStackTrace();
                        }
                    });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    public static synchronized Connection getConnection() throws SQLException {
        if(dataSource == null){
            init();
        }
        return dataSource.getConnection();
    }
}
