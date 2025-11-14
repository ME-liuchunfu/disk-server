package com.spring.boot.disk.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiskServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiskServerApplication.class, args);
		System.out.println("程序启动完成");
	}

}
