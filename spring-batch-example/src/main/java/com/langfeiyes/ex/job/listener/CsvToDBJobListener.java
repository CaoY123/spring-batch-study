package com.langfeiyes.ex.job.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;


//统计数据读与数据写 花费多长时间
public class CsvToDBJobListener  implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        //begin--作业上下文数据共享
        long begin = System.currentTimeMillis();
        jobExecution.getExecutionContext().putLong("begin", begin);
        System.err.println("-------------------------【CsvToDBJob开始时间：】---->"+begin+"<-----------------------------");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        //end
        long end = System.currentTimeMillis();
        long begin = jobExecution.getExecutionContext().getLong("begin");
        //总消耗时间： end - begin
        System.err.println("-------------------------【CsvToDBJob结束时间：】---->"+end+"<-----------------------------");
        System.err.println("-------------------------【CsvToDBJob总耗时：】---->"+(end - begin)+" 毫秒<-----------------------------");
    }
}
