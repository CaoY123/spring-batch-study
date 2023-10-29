package com.langfeiyes.batch._09_step_listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

//自定义step执行监听器
public class MyStepListener  implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("-----------beforeStep--------->");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("-----------afterStep--------->");
        return stepExecution.getExitStatus();  //必要改动
    }
}
