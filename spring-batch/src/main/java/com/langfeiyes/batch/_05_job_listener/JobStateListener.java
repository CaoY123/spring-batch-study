package com.langfeiyes.batch._05_job_listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * 自定义作业状态监听器
 * 作用：用于记录作业执行前， 执行后状态信息
 */
public class JobStateListener implements JobExecutionListener {

    //作业执行前执行
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.err.println("作业执行前的状态：" + jobExecution.getStatus());
    }

    //作业执行后执行
    @Override
    public void afterJob(JobExecution jobExecution) {
        System.err.println("作业执行后的状态：" + jobExecution.getStatus());
    }
}
