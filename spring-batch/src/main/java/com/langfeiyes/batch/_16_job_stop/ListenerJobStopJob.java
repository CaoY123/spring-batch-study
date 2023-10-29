package com.langfeiyes.batch._16_job_stop;

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
public class ListenerJobStopJob {

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
                    ResouceCount.readCount++;  //50
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
                System.err.println("readCount:" + ResouceCount.readCount + ", totalCount:" + ResouceCount.totalCount);

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    @Bean
    public StopStepListener stopStepListener(){
        return new StopStepListener();
    }

    //构造一个step对象
    @Bean
    public Step step1(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet1())
                .listener(stopStepListener())
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
        return jobBuilderFactory.get("step-stop-job")
                .start(step1())
                //当step1 返回的是STOPPED状态码，马上结束作业流程，设置流程状态为：STOPPED，并设置重启，从step1位置开始执行
                .on("STOPPED").stopAndRestart(step1())
                //如果step1执行结果不是STOPPED 而是其他状态， 表示满足判断条件，然后执行step2步骤
                .from(step1()).on("*").to(step2())
                .end()
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ListenerJobStopJob.class, args);
    }
}
