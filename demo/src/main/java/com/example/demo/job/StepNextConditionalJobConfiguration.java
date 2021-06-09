package com.example.demo.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StepNextConditionalJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job stepNextConditionalJob() {
		return jobBuilderFactory.get("stepNextConditionalJob")
				.start(conditionalJobStep1())
				.on("FAILED") // FAILED 일 경우
				.to(conditionalJobStep3()) // step3으로 이동한다.
				.on("*") // step3의 결과 관계 없이
				.end() // step3으로 이동하면 Flow가 종료된다.
				.from(conditionalJobStep1()) // step1로 부터
				.on("*") // FAILED 이외에 모든 경우
				.to(conditionalJobStep2()) // step2로 이동한다.
				.next(conditionalJobStep3()) // step2가 정상 종료되면 step3으로 이동한다.
				.on("*") // step3의 결과 관계 없이
				.end() // step3으로 이동하면 Flow가 종료한다.
				.end() // Job 종료
				.build();
	}

	@Bean
	public Step conditionalJobStep1() {
		// TODO Auto-generated method stub
		return stepBuilderFactory.get("step1").tasklet((contribution, chunkContext) -> {
			log.info(">>>>> This is stepNextConditionalJob Step1");

			/*
			 * ExutStatus를 Failed로 지정한다. 해당 status를 보고 flow가 진행된다.
			 */
//			contribution.setExitStatus(ExitStatus.FAILED);

			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step conditionalJobStep2() {
		// TODO Auto-generated method stub
		return stepBuilderFactory.get("step2").tasklet((contribution, chunkContext) -> {
			log.info(">>>>> This is stepNextConditionalJob Step2");

			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step conditionalJobStep3() {
		// TODO Auto-generated method stub
		return stepBuilderFactory.get("step3").tasklet((contribution, chunkContext) -> {
			log.info(">>>>> This is stepNextConditionalJob Step3");

			return RepeatStatus.FINISHED;
		}).build();
	}
}
