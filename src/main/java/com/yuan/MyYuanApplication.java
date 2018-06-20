package com.yuan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yuan.dao")
public class MyYuanApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyYuanApplication.class, args);
	}
}
