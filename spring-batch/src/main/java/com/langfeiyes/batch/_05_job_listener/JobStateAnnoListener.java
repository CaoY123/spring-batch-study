package com.langfeiyes.batch._05_job_listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

/**
 * 作业状态监听器--使用注解方式实现
 */
public class JobStateAnnoListener {
    @BeforeJob
    //作业执行前执行
    public void beforeJob(JobExecution jobExecution) {
        System.err.println("作业执行前的状态：" + jobExecution.getStatus());
    }
    @AfterJob
    //作业执行后执行
    public void afterJob(JobExecution jobExecution) {
        System.err.println("作业执行后的状态：" + jobExecution.getStatus());
    }
}
