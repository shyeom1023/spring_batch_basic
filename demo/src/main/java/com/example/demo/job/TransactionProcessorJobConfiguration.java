package com.example.demo.job;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.ClassInformation;
import com.example.demo.domain.Teacher;
import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TransactionProcessorJobConfiguration {

    public static final String JOB_NAME = "transactionProcessorBatch";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;
	
    List<String> list = new ArrayList<>();

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
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Teacher, ClassInformation>chunk(chunkSize)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


    @Bean
    public JpaPagingItemReader<Teacher> reader() {
    	 
    	 
        return new JpaPagingItemReaderBuilder<Teacher>()
                .name(BEAN_PREFIX+"reader")
                .entityManagerFactory(emf)
                .pageSize(chunkSize)
                .queryString("SELECT t FROM Teacher t")
                .build();
    }

    public ItemProcessor<Teacher, ClassInformation> processor() {
    	
        return new ItemProcessor<Teacher, ClassInformation>() {
			@Override
			public ClassInformation process(Teacher teacher) throws Exception {

				String value = (teacher.getId()+","+teacher.getName());
				list.add(value);
				System.out.println(value);
				
				return new ClassInformation(teacher.getName(), teacher.getStudents().size());
			}
		};
    }

    private ItemWriter<ClassInformation> writer() {
    	try {
			writeDataToCsv(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return items -> {
            log.info(">>>>>>>>>>> Item Write");
            
            for (ClassInformation item : items) {
                //log.info("반 정보= {}", item);
                System.out.println(item);
                
            }    
        };
    }
    
    public static void writeDataToCsv(List<String> value) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter("./sample.csv"));
        
        for(String list : value) {
        System.out.println("writeDataToCsv = "+list);
        writer.writeNext(list.split(","));
        }

        writer.close();
    }
}