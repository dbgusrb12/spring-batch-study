package com.hg.batch.chunk;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class ChunkBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ChunkBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job chunkJob() {
        return new JobBuilder("chunkJob", jobRepository)
                .start(chunkStep())
                .build();
    }

    @Bean
    public Step chunkStep() {
        return new StepBuilder("chunkStep", jobRepository)
                // chunk 방식 사용 (chunk size 는 10으로 설정)
                .<Integer, String>chunk(10, transactionManager)
                .reader(itemReader()) // ItemReader 구현체 등록
                .processor(itemProcessor()) // ItemProcessor 구현체 등록
                .writer(itemWriter()) // ItemWriter 구현체 등록
                .build();
    }

    @Bean
    public ItemReader<Integer> itemReader() {
        return new ListItemReader(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Bean
    public ItemProcessor<Integer, String> itemProcessor() {
        return new FunctionItemProcessor<>(integer -> {
            System.out.println("가공 할 element : " + integer);
            return integer.toString();
        });
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return new ListItemWriter();
    }
}
