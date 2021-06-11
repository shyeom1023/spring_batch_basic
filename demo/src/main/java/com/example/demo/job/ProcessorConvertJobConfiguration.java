package com.example.demo.job;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.Teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorConvertJobConfiguration {
	
	public static final String JOB_NAME = "ProcessorConvertBatch";
	public static final String BEAN_PREFIX = JOB_NAME + "_";
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory emf;
	
	@Value("${chunkSize:1000}")
	private int chunkSize;
	
	@Bean(JOB_NAME)
	public Job job() {
		return jobBuilderFactory.get(JOB_NAME)
				.preventRestart()
				.start(step())
				.build();
	}

	@Bean(BEAN_PREFIX + "step")
	@JobScope
	public Step step() {
		// TODO Auto-generated method stub
		return stepBuilderFactory.get(BEAN_PREFIX+"step")
				.<Teacher, String>chunk(chunkSize)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}

	private ItemWriter<String> writer() {
		// TODO Auto-generated method stub
		return items ->{
			for(String item : items) {
				log.info("Teacher Name={}", item);
			}
		};
	}

	@Bean
	public ItemProcessor<Teacher,String> processor() {
		// TODO Auto-generated method stub
		return teacher -> {
			return teacher.getName();
		};
	}

	@Bean
	public JpaPagingItemReader<Teacher> reader() {
		// TODO Auto-generated method stub
		return new JpaPagingItemReaderBuilder<Teacher>()
				.name(BEAN_PREFIX+"reader")
				.entityManagerFactory(emf)
				.pageSize(chunkSize)
				.queryString("SELECT t FROM Teacher t")
				.build();
	}
	
	
	

}
