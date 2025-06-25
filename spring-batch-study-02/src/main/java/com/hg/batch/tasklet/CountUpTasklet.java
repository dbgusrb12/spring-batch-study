package com.hg.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class CountUpTasklet implements Tasklet {

    private final int maxCount;
    private int currentCount = 0;

    public CountUpTasklet(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        currentCount++;
        System.out.println("현재 카운트 (" + currentCount + "/" + maxCount + ")");
        if (currentCount >= maxCount) {
            System.out.println("카운트 세기 완료.");
            return RepeatStatus.FINISHED; // 카운트를 다 셈.
        }
        return RepeatStatus.CONTINUABLE; // 아직 셀 카운트가 더 있음.
    }
}
