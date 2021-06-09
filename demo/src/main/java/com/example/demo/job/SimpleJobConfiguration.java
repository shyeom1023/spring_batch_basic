package com.example.demo.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration // 그냥 configuration ㅎㅎ
public class SimpleJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job simpleJob() {
		JobBuilder jobBuilder = jobBuilderFactory.get("simpleJob");
		SimpleJobBuilder start = jobBuilder.start(simpleStep1(null));
		SimpleJobBuilder next = start.next(simpleStep2(null));
		Job build = next.build();
		return build;

	}

	@Bean
	@JobScope
	public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
		return stepBuilderFactory.get("simpleStep1").tasklet((contribution, chunkContext) -> {
			log.info(">>>>> This is Step1");
			log.info(">>>>> requestDate = {}", requestDate);
			return RepeatStatus.FINISHED;
		}).build();
	}
	
	
	@Bean
	@JobScope
	public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
		return stepBuilderFactory.get("simpleStep2").tasklet((contribution, chunkContext) -> {
			log.info(">>>>>> This is Step2");
			log.info(">>>>>> requestDate = {}",requestDate);
			return RepeatStatus.FINISHED;
		}).build();
	}
	
	

}
