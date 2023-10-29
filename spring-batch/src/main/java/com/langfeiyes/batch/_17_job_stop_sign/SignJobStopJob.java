package com.langfeiyes.batch._17_job_stop_sign;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//开启 spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
//springboot 项目，启动注解， 保证当前为为启动类
@SpringBootApplication
public class SignJobStopJob {

    //作业启动器
    @Autowired
    private JobLauncher jobLauncher;

    //job构造工厂---用于构建job对象
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //step 构造工厂--用于构造step对象
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //模拟从数据库查询数据
    private int readCountDB = 100;

    //构造一个step对象执行的任务（逻辑对象）
    @Bean
    public Tasklet tasklet1(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("----------------step1---------------");
                for (int i = 1; i <= readCountDB; i++) {
                    ResouceCount.readCount ++;  //50
                }

                //如果不满足条件： readCount ！= totalCount 设置停止标记
                if(ResouceCount.readCount != ResouceCount.totalCount){
                    //停止标记
                    chunkContext.getStepContext().getStepExecution().setTerminateOnly();
                }

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    @Bean
    public Tasklet tasklet2(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                System.err.println("step2执行了.......");
                System.err.println("readCount:" + com.langfeiyes.batch._16_job_stop.ResouceCount.readCount + ", totalCount:" + ResouceCount.totalCount);

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    //构造一个step对象
    @Bean
    public Step step1(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet1())
                .allowStartIfComplete(true)  //运行step从新执行
                .build();
    }

    //构造一个step对象
    @Bean
    public Step step2(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet2())
                .build();
    }

    @Bean
    public  Job job(){
        return jobBuilderFactory.get("sign-step-stop-job")
                .start(step1())
                .next(step2())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SignJobStopJob.class, args);
    }
}
