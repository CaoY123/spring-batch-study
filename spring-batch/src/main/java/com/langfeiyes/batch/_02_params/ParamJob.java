package com.langfeiyes.batch._02_params;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
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
public class ParamJob {

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
    @StepScope
    @Bean
    public Tasklet tasklet(@Value("#{jobParameters['name']}")String name){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                //要执行逻辑--step步骤执行逻辑
                //方案1： 使用chunkContext
                //Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                //System.out.println("params---name:" + jobParameters.get("name"));

                //方案2： 使用@Vlaue
                System.out.println("params---name:" + name);
                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }


    //构造一个step对象
    @Bean
    public Step step1(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory
                .get("step1")
                .tasklet(tasklet(null))
                .build();
    }


    //构造一个job对象
   /* @Bean
    public  Job job(){
        return jobBuilderFactory.get("param-job").start(step1()).build();
    }*/
    /*@Bean
    public  Job job(){
        return jobBuilderFactory.get("param-chunk-job").start(step1()).build();
    }*/
    @Bean
    public  Job job(){
        return jobBuilderFactory
                .get("param-value-job1")
                .start(step1())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ParamJob.class, args);
    }
}
