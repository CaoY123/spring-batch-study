package com.langfeiyes.batch._04_param_incr;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

//开启 spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
//springboot 项目，启动注解， 保证当前为为启动类
@SpringBootApplication
public class IncrementParamJob {

    //作业启动器
    @Autowired
    private JobLauncher jobLauncher;

    //job构造工厂---用于构建job对象
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //step 构造工厂--用于构造step对象
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //构造一个step对象执行的任务（逻辑对象）
    @Bean
    public Tasklet tasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                //要执行逻辑--step步骤执行逻辑
                chunkContext.getStepContext().getStepExecution().getExecutionContext();
                chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();

                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
//                System.out.println("params---run.id:" + jobParameters.get("run.id"));
                System.out.println("params---daily:" + jobParameters.get("daily"));

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    @Bean
    public DailyTimestampParamIncrementer dailyTimestampParamIncrementer(){
        return new DailyTimestampParamIncrementer();
    }

    //构造一个step对象
    @Bean
    public Step step1(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory
                .get("step1")
                .tasklet(tasklet())
                .build();
    }

    @Bean
    public  Job job(){
        return jobBuilderFactory
                .get("incr-params-job")
                .start(step1())
//                .incrementer(new RunIdIncrementer())  //run.id自增
                .incrementer(dailyTimestampParamIncrementer())  //时间戳的自增
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(IncrementParamJob.class, args);
    }
}
