package com.hg.batch.pojo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class PojoBatchConfig {

    @Bean
    public Job pojoJob(JobRepository jobRepository, Step pojoStep) {
        return new JobBuilder("pojoJob", jobRepository)
                .start(pojoStep)
                .build();
    }

    @Bean
    public Step pojoStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet pojoTasklet) {
        return new StepBuilder("pojoStep", jobRepository)
                .tasklet(pojoTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet pojoTasklet(PojoJobParameters pojoJobParameters) {
        return (contribution, chunkContext) -> {
            log.info("pojoTasklet 실행");
            log.info("pojoJobParameters: {}", pojoJobParameters);
            log.info("pojoTasklet 종료");
            return RepeatStatus.FINISHED;
        };
    }
}
