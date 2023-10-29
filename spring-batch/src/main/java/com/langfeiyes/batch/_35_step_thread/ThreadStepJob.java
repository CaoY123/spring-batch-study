package com.langfeiyes.batch._35_step_thread;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;


//读user.txt文件封装user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class ThreadStepJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
            }
        };
    }

    @Bean
    public FlatFileItemReader<User> itemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .saveState(false) //防止状态被覆盖
                //获取文件
                .resource(new ClassPathResource("user-thread.txt"))
                //解析数据--指定解析器使用# 分割--默认是 ，号
                .delimited().delimiter("#")
                //按照 # 截取数据之后， 数据怎么命名
                .names("id", "name", "age")
                //封装数据--将读取的数据封装到对象：User对象
                .targetType(User.class)
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1) //一次读多少数据
                .reader(itemReader())
                .writer(itemWriter())
                .taskExecutor(new SimpleAsyncTaskExecutor())  //开启多线程操作
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("thread-step-job32")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ThreadStepJob.class, args);
    }
}
