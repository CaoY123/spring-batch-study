package com.langfeiyes.batch._03_param_validator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Map;

//开启 spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
//springboot 项目，启动注解， 保证当前为为启动类
@SpringBootApplication
public class ParamValidatorJob {

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
                //方案1： 使用chunkContext
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                System.out.println("params--必填--name:" + jobParameters.get("name"));
                System.out.println("params--可选--age:" + jobParameters.get("age"));
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
                .tasklet(tasklet())
                .build();
    }

    //指定参数校验器
    @Bean
    public NameParamValidator nameParamValidator(){
        return new NameParamValidator();
    }

    //默认参数校验器
    @Bean
    public DefaultJobParametersValidator defaultJobParametersValidator(){
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
        //必传参数
        validator.setRequiredKeys(new String[]{"name"});   //必须传name参数
        //可选参数
        validator.setOptionalKeys(new String[]{"age"});  //age是可选的
        return validator;
    }

    //组合参数校验器
    @Bean
    public CompositeJobParametersValidator compositeJobParametersValidator() throws Exception {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(nameParamValidator(), defaultJobParametersValidator()));
        validator.afterPropertiesSet();
        return validator;
    }


    //构造一个job对象
   /* @Bean
    public  Job job(){
        return jobBuilderFactory.get("name-param-validate-job")
                .start(step1())
                .validator(nameParamValidator())  //指定参数校验器
                .build();
    }*/

   /* @Bean
    public  Job job(){
        return jobBuilderFactory.get("default-name-age-param-validate-job")
                .start(step1())
                .validator(defaultJobParametersValidator())
                .build();
    }*/

    @Bean
    public  Job job() throws Exception {
        return jobBuilderFactory.get("composite--param-validate-job3")
                .start(step1())
                .validator(compositeJobParametersValidator()) // 指定参数的校验器
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ParamValidatorJob.class, args);
    }
}
