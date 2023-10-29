package com.langfeiyes.batch._11_step_condition_decider;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
public class CustomizeStatusStepJob {

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
    public Tasklet firstTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("----------------firstTasklet---------------");
                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    @Bean
    public Tasklet taskletA(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                System.out.println("----------------taskletA--------------");

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }
    @Bean
    public Tasklet taskletB(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                System.out.println("----------------taskletB---------------");

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    @Bean
    public Tasklet taskletDefault(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                System.out.println("----------------taskletDefault---------------");

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    //构造一个step对象
    @Bean
    public Step firstStep(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("firstStep")
                .tasklet(firstTasklet())
                .build();
    }
    //构造一个step对象
    @Bean
    public Step stepA(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("stepA")
                .tasklet(taskletA())
                .build();
    }

    //构造一个step对象
    @Bean
    public Step stepB(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("stepB")
                .tasklet(taskletB())
                .build();
    }

    @Bean
    public Step stepDefault(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("stepDefault")
                .tasklet(taskletDefault())
                .build();
    }


    @Bean
    public MyStatusDecider statusDecider(){
        return new MyStatusDecider();
    }

    /**
     * if("A".eqauls(statusDecider()){
     *     .to(stepA())
     * }else if("B".eqauls(statusDecider()){
     *     .to(stepB())
     * }else{
     *     .to(stepDefault())
     * }
     */

    @Bean
    public  Job job(){
        return jobBuilderFactory.get("step-multi-job")
                .start(firstStep())
                .next(statusDecider())
                .from(statusDecider()).on("A").to(stepA())
                .from(statusDecider()).on("B").to(stepB())
                .from(statusDecider()).on("*").to(stepDefault())
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CustomizeStatusStepJob.class, args);
    }
}
