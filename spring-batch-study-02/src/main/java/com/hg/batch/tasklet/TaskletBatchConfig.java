package com.hg.batch.tasklet;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TaskletBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public TaskletBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Tasklet countUpTasklet() {
        return new CountUpTasklet(10);
    }

    @Bean
    public Job countUpJob() {
        return new JobBuilder("taskletJob", jobRepository)
                .start(countUpStep()) // step 등록
                .build();
    }

    @Bean
    public Step countUpStep() {
        return new StepBuilder("taskletStep", jobRepository)
                // tasklet 방식 사용
                .tasklet(countUpTasklet(), transactionManager)
                .build();
    }
}
