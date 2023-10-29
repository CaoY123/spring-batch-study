package com.langfeiyes.batch._26_itemprocessor_validate;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;


//读user.txt文件封装user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class ValidationProcessorJob {

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

    //BeanValidatingItemProcessor 是 ValidatingItemProcessor 子类
    @Bean
    public BeanValidatingItemProcessor<User>  beanValidatingItemProcessor(){
        BeanValidatingItemProcessor itemProcessor = new BeanValidatingItemProcessor();
        itemProcessor.setFilter(true);  //如果不满足数据直接抛弃
        return  itemProcessor;
    }

    @Bean
    public FlatFileItemReader<User> itemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                //获取文件
                .resource(new ClassPathResource("users-validate.txt"))
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
                .processor(beanValidatingItemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("validate-processor-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ValidationProcessorJob.class, args);
    }
}
