package com.langfeiyes.batch._13_flow_step;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
public class FlowStepJob {

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
    public Tasklet taskletA(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("----------------taskletA---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Tasklet taskletB1(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("------------stepB----taskletB1---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Tasklet taskletB2(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("------------stepB----taskletB2---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }
    @Bean
    public Tasklet taskletB3(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("------------stepB----taskletB3---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Tasklet taskletC(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("----------------taskletC---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    //构造一个step对象
    @Bean
    public Step stepA(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("stepA")
                .tasklet(taskletA())
                .build();
    }

    @Bean
    public Step stepB1(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("stepB1")
                .tasklet(taskletB1())
                .build();
    }

    @Bean
    public Step stepB2(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("stepB2")
                .tasklet(taskletB2())
                .build();
    }

    @Bean
    public Step stepB3(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("stepB3")
                .tasklet(taskletB3())
                .build();
    }

    //构造一个流式步骤
    @Bean
    public Flow flowB(){
        return new FlowBuilder<Flow>("flowB")
                .start(stepB1())
                .next(stepB2())
                .next(stepB3())
                .build();
    }

    //job 没有现有的flowStep步骤操作方法， 必须使用step进行封装之后再执行
    @Bean
    public Step stepB(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory
                .get("stepB")
                .flow(flowB())
                .build();
    }

    //构造一个step对象
    @Bean
    public Step stepC(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("stepC")
                .tasklet(taskletC())
                .build();
    }

    //如果firstStep 执行成功：下一步执行successStep 否则是failStep
    @Bean
    public  Job job(){
        return jobBuilderFactory
                .get("flow-step-job")
                .start(stepA())
                .next(stepB())
                .next(stepC())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FlowStepJob.class, args);
    }
}
