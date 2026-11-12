package com.int_solutions.sys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SysApplication {
	public static void main(String[] args) {
		SpringApplication.run(SysApplication.class, args);
	}

}
