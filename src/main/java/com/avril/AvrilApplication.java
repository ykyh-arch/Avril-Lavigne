package com.avril;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ykyh-arch
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class AvrilApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(AvrilApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  服务启动成功   ლ(´ڡ`ლ)ﾞ  \n"  );
    }
}