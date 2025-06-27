package com.hg.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class BatchConfig {

    private AtomicInteger loopCount = new AtomicInteger(0);
    private final int LOOP_TARGET = 5;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job helloBatchJob() {
        return new JobBuilder("helloBatchJob", jobRepository)
                .start(enterBatchStep())
                .next(greetBatchStep())
                .next(loopBatchStep())
                .next(completeBatchStep())
                .build();
    }

    @Bean
    public Step enterBatchStep() {
        return new StepBuilder("enterBatchStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Enter Batch Step !!");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step greetBatchStep() {
        return new StepBuilder("greetBatchStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Greet Batch Step !!");
                    System.out.println("배치 시작하기 : " + LOOP_TARGET + "번의 루프를 도는 배치");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step loopBatchStep() {
        return new StepBuilder("loopBatchStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int currentLoopCount = this.loopCount.incrementAndGet();
                    System.out.println("루프 돌기 : 현재 : " + currentLoopCount + " / " + LOOP_TARGET);
                    if (currentLoopCount < LOOP_TARGET) {
                        return RepeatStatus.CONTINUABLE;
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step completeBatchStep() {
        return new StepBuilder("completeBatchStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Complete Batch Step !!");
                    System.out.println("배치 완료");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
}
