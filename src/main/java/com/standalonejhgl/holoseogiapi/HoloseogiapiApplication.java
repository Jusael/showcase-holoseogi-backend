package com.standalonejhgl.holoseogiapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.standalonejhgl.holoseogiapi.daos")
@EnableScheduling
@EnableAsync
@EnableCaching
public class HoloseogiapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoloseogiapiApplication.class, args);
	}

}
