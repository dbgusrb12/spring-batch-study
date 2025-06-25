package com.hg.batch.datetime;

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
public class DateTimeBatchConfig {

    @Bean
    public Job dateTimeJob(JobRepository jobRepository, Step dateTimeStep) {
        return new JobBuilder("dateTimeJob", jobRepository)
                .start(dateTimeStep)
                .build();
    }

    @Bean
    public Step dateTimeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet dateTimeTasklet) {
        return new StepBuilder("dateTimeStep", jobRepository)
                .tasklet(dateTimeTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet dateTimeTasklet(
            @Value("#{jobParameters['executionDate']}") LocalDate executionDate,
            @Value("#{jobParameters['startTime']}") LocalDateTime startTime
    ) {
        return (contribution, chunkContext) -> {
            log.info("dateTimeTasklet 실행");
            log.info("executionDate: {}", executionDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
            log.info("startTime: {}", startTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));

            LocalDateTime currentTime = startTime;
            for (int i = 1; i <= 3; i++) {
                currentTime = currentTime.plusHours(1);
                log.info("{}시간 경과... 현재 시각: {}", i, currentTime.format(DateTimeFormatter.ofPattern("HH시 mm분")));
            }

            log.info("dateTimeTasklet 종료");
            log.info("종료 시간: {}", currentTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));

            return RepeatStatus.FINISHED;
        };

    }
}
