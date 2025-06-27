package com.hg.batch.json;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.converter.JsonJobParametersConverter;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.batch.job", name = "name", havingValue = "jsonJob")
// batch name 이 jsonJob 일 때만 설정
public class JsonBatchConfig {

    @Bean
    public JobParametersConverter jsonJobParameterConverter() {
        // Json parameter 를 받기 위한 Converter
        return new JsonJobParametersConverter();
    }

    @Bean
    public Job jsonJob(JobRepository jobRepository, Step jsonStep) {
        return new JobBuilder("jsonJob", jobRepository)
                .start(jsonStep)
                .build();
    }

    @Bean
    public Step jsonStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet jsonTasklet) {
        return new StepBuilder("jsonStep", jobRepository)
                .tasklet(jsonTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet jsonTasklet(
            @Value("#{jobParameters['name']}") String name
    ) {
        return (contribution, chunkContext) -> {
            log.info("jsonTasklet 실행");
            log.info("name: {}", name);
            log.info("jsonTasklet 종료");
            return RepeatStatus.FINISHED;
        };
    }
}
