package com.langfeiyes.batch._01_hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.ResponseCache;

//开启 spring batch 注解--可以让 spring 容器创建 springbatch 操作相关类对象
@EnableBatchProcessing
//springboot 项目，启动注解， 保证当前为为启动类
@SpringBootApplication
public class HelloJob {

    // 作业启动器，看起来下面的 jobLaunch 变量没有被使用，但是实际上在 spring-batch 内部使用了，
    // 只是这个案例比较简单，没有在表面上使用而已。
    @Autowired
    private JobLauncher jobLauncher;

    //job构造工厂---用于构建job对象
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //step 构造工厂--用于构造step对象
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    //构造一个step对象执行的任务（逻辑对象），类似于 Thread 的 Runnable 接口
    @Bean
    public Tasklet tasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                //要执行逻辑--step步骤执行逻辑
                System.out.println("hello spring  batch！");
                return RepeatStatus.FINISHED;  //执行完了，返回一个状态
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


    //构造一个job对象
    @Bean
    public  Job job(){
        return jobBuilderFactory
                .get("hello-job2")
                .start(step1()) // 如果有两个步骤，就用 next(step) 来连接
                .build();
    }


    public static void main(String[] args) {
        // 注：如果运行该入门案例的时候要使用内置 H2 数据库，要打开其依赖而关闭 MySQL 的依赖，否则会报错。
        // 如果要用 MySQL，则需要开启 MySQL 的依赖，关于 H2 的依赖，并注意要配置好正确的数据库连接信息
        // （即：username、password、url、driver-class-name），之后根据 yml 文件中的配置信息，在
        // 项目启动后会自动执行位于 jar 包中的 sql 脚本 schema-mysql.sql（可以全局搜到该文件），就会
        // 自动在库里建表。注意：第一次在没有在库里建表的时候运行该案例不会报错，第二次会报“表已经存在”的错，
        // 需要我们将相应的创建模式更改为 spring.sql.init.mode 更改为 never。
        SpringApplication.run(HelloJob.class, args);
        // 还要注意，第一次运行该程序输出 “Hello ...” 后，第二次就不会输出了，这个是正常的（即：当我们执行
        // 完一次作业的流程，下次在执行的时候就不会再重新执行这个作业了），至于是什么原因，其实就是 spring
        // 认为同一个批处理只需执行一般即可，如果第二次的时候再来，而且还有一样的参数，就不会执行该批处理，所
        // 以要想再次执行该批处理，需要更改 job 的名称或是改变 job 的参数。
    }
}
