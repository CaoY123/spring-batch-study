package com.langfeiyes.batch._16_job_stop;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class StopStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        //不满足条件处理
        if(ResouceCount.readCount != ResouceCount.totalCount){
            return ExitStatus.STOPPED;
        }

        return stepExecution.getExitStatus();
    }
}
