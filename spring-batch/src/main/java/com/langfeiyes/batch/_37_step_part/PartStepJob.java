package com.langfeiyes.batch._37_step_part;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

@SpringBootApplication
@EnableBatchProcessing
public class PartStepJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //编写从步骤--工作步骤--分区步骤--读操作
    @Bean
    @StepScope
    //多个从步骤共有同一个itemreader组件， 不能写死操作文件资源，需要使用变量方式动态指定
    //@Value("#{stepExecutionContext['file']}")  从步骤的上下文路径中获取要读的资源文件对象
    //能实现的前提： 步骤上下文中必须有值，这些数据值是在 主步骤的分区器中设置。
    public FlatFileItemReader<User> itemReader(@Value("#{stepExecutionContext['file']}")Resource resource){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                //获取文件
                .resource(resource)
                //解析数据--指定解析器使用# 分割--默认是 ，号
                .delimited().delimiter("#")
                //按照 # 截取数据之后， 数据怎么命名
                .names("id", "name", "age")
                //封装数据--将读取的数据封装到对象：User对象
                .targetType(User.class)
                .build();
    }

    //编写从步骤--工作步骤--分区步骤--写操作
    @Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
            }
        };
    }

    //编写从步骤--工作步骤--分区步骤-
    @Bean
    public Step workStep(){
        return  stepBuilderFactory.get("workStep")
                .<User, User>chunk(10)
                .reader(itemReader(null))
                .writer(itemWriter())
                .build();
    }

    //定义分区器
    @Bean
    public UserPartitioner userPartitioner(){
        return new UserPartitioner();
    }

    //定义分区处理器
    @Bean
    public PartitionHandler partitionHandler(){
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        //指定要创建分区步骤/从步骤有几个
        handler.setGridSize(5);
        //一个分区步骤/从步骤 是一个独立线程
        handler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        //关联从步骤
        handler.setStep(workStep());
        try {
            handler.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler;
    }

    //主步骤
    @Bean
    public Step masterStep(){
        return stepBuilderFactory.get("masterStep")
                .partitioner(workStep().getName(), userPartitioner())  //分区器
                .partitionHandler(partitionHandler()) //分区处理器
                .build();
    }

    //定义作业
    @Bean
    public Job job(){
        return jobBuilderFactory.get("part-step-job")
                .start(masterStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(PartStepJob.class, args);
    }
}
