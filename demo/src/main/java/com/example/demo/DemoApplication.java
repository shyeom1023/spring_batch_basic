package com.example.demo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing	// 배치기능 활성화
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		//SpringApplication.run(DemoApplication.class, args);
		
		SpringApplication application =
				new SpringApplication(DemoApplication.class);
		
		// WebApplicationType.NONE  설정하면 자바 애플릿으로 실행됨.
		application.setWebApplicationType(WebApplicationType.NONE);
		application.setBannerMode(Banner.Mode.OFF);
		application.run(args);
	}

}
