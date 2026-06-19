package com.airport.fod;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.airport.fod.mapper")
public class FodInspectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FodInspectionApplication.class, args);
        System.out.println("机场跑道异物巡查系统启动成功！");
    }
}
