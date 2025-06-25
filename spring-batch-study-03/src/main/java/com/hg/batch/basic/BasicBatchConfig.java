package com.hg.batch.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class BasicBatchConfig {

    @Bean
    public Job basicJob(JobRepository jobRepository, Step basicStep) {
        return new JobBuilder("basicJob", jobRepository)
                .start(basicStep)
                .build();
    }

    @Bean
    public Step basicStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet basicTasklet) {
        return new StepBuilder("basicStep", jobRepository)
                .tasklet(basicTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet basicTasklet(
            @Value("#{jobParameters['basicId']}") String basicId,
            @Value("#{jobParameters['count']}") Integer count
    ) {
        return (contribution, chunkContext) -> {
            log.info("basicTasklet 실행");
            log.info("ID: {}", basicId);
            log.info("count: {}", count);
            log.info("basic tasklet countUp 실행");
            for (int i = 0; i < count; i++) {
                log.info("countUp!! {}", i);
            }
            log.info("basicTasklet 종료");
            return RepeatStatus.FINISHED;
        };
    }
}
