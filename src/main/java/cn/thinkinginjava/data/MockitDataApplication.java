package cn.thinkinginjava.data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@MapperScan(basePackages = {"cn.thinkinginjava.data.mapper"})
public class MockitDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockitDataApplication.class, args);
    }

}
