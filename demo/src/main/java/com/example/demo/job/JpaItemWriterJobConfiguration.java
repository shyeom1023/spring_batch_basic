package com.example.demo.job;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.Pay;
import com.example.demo.domain.Pay2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaItemWriterJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private static final int chunkSize = 10;
    
    @Bean
    public Job jpaItemWriterJob() {
    	return jobBuilderFactory.get("jpaItemWriterJob")
    			.start(jpaItemWriterStep())
    			.build();
    }

    @Bean
	public Step jpaItemWriterStep() {
		// TODO Auto-generated method stub
		return stepBuilderFactory.get("jpaItemWriterStep")
				.<Pay, Pay2>chunk(chunkSize)
				.reader(jpaItemWriterReader())
				.processor(jpaItemProcessor())
				.writer(jpaItemWriter())
				.build();
	}

    @Bean
    public JpaItemWriter<Pay2> jpaItemWriter() {
		// TODO Auto-generated method stub
    	JpaItemWriter<Pay2> jpaItemWriter = new JpaItemWriter<>();
    	jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    	
		return jpaItemWriter;
				
	}

	@Bean
    public ItemProcessor<Pay,Pay2> jpaItemProcessor() {
		// TODO Auto-generated method stub
		return pay -> new Pay2(pay.getAmount(), pay.getTxName(), pay.getTxDateTime() );
	}

	@Bean
	public JpaPagingItemReader<Pay> jpaItemWriterReader() {
		// TODO Auto-generated method stub
		return new JpaPagingItemReaderBuilder<Pay>()
				.name("jpaItemWriterReader")
				.entityManagerFactory(entityManagerFactory)
				.pageSize(chunkSize)
				.queryString("SELECT p FROM Pay p WHERE amount >= 2000 ORDER BY id")
				.build();
	}

}
