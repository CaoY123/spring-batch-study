package com.langfeiyes.batch._14_job_start_test;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
// 将 App 作为下面测试类的配置类，确保扫描到
// Spring 的 Test 环境中不允许使用 bean 的方式管理对象，所以需要将下面的方法当做一个普通方法
@SpringBootTest(classes = App.class)
public class StartJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    //job构造工厂---用于构建job对象
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //step 构造工厂--用于构造step对象
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    //构造一个step对象执行的任务（逻辑对象）
    public Tasklet tasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                //要执行逻辑--step步骤执行逻辑
                System.out.println("hello spring  batch！");
                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    //构造一个step对象
    public Step step1(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory
                .get("step1")
                .tasklet(tasklet())
                .build();
    }

    //构造一个job对象
    public Job job(){
        return jobBuilderFactory
                .get("start-test-job")
                .start(step1())
                .build();
    }

    // 实际的单元测试类的启动类
    @Test
    public void testStart() throws Exception {
        // 用 jobLaunch 启动，传一个空的作业参数集
        jobLauncher.run(job(), new JobParameters());
    }
}
