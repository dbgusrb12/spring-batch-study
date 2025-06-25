package com.hg.batch.enumeration;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
public class EnumBatchConfig {

    @Bean
    public Job enumJob(JobRepository jobRepository, Step enumStep) {
        return new JobBuilder("enumJob", jobRepository)
                .start(enumStep)
                .build();
    }

    @Bean
    public Step enumStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet enumTasklet) {
        return new StepBuilder("enumStep", jobRepository)
                .tasklet(enumTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet enumTasklet(
            @Value("#{jobParameters['color']}") Color color
    ) {
        return (contribution, chunkContext) -> {
            log.info("enumTasklet 실행");
            log.info("color: {}", color);

            int colorNum = switch (color) {
                case RED -> 1;
                case BLUE -> 2;
                case YELLOW -> 3;
                case GREEN -> 4;
            };
            log.info("colorNum: {}", colorNum);
            log.info("enumTasklet 종료");
            return RepeatStatus.FINISHED;
        };

    }
}
