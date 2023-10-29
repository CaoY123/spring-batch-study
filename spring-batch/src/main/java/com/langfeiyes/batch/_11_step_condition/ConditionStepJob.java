package com.langfeiyes.batch._11_step_condition;

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
public class ConditionStepJob {

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

//                throw new RuntimeException("假装失败了");
                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    @Bean
    public Tasklet successTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                System.out.println("----------------successTasklet--------------");

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }
    @Bean
    public Tasklet failTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                System.out.println("----------------failTasklet---------------");

                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }



    //构造一个step对象
    @Bean
    public Step firstStep(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory
                .get("firstStep")
                .tasklet(firstTasklet())
                .build();
    }
    //构造一个step对象
    @Bean
    public Step successStep(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory
                .get("successStep")
                .tasklet(successTasklet())
                .build();
    }

    //构造一个step对象
    @Bean
    public Step failStep(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory
                .get("failStep")
                .tasklet(failTasklet())
                .build();
    }

    //如果firstStep 执行成功：下一步执行successStep 否则是failStep
    @Bean
    public  Job job(){

        /**
         * 假设firstStep() 返回状态变量是： a
         *   if( a.equals("FAILED"))
         *      .to(failStep())
         *   else
         *      .to(successStep())
         */

        return jobBuilderFactory
                .get("step-multi-job")
                //满足xxx条件执行后续逻辑--当前意思：满足firstStep执行返回状态为失败状态：FAILED执行failStep
                .start(firstStep()).on("FAILED").to(failStep())
                // * 表示else 逻辑
                .from(firstStep()).on("*").to(successStep())
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConditionStepJob.class, args);
    }
}
