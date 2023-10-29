package com.langfeiyes.batch._29_itemprocessor_composite;



import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;


//读user.txt文件封装user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class CompositeProcessorJob {

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

    //参数校验
    @Bean
    public BeanValidatingItemProcessor<User>  beanValidatingItemProcessor(){
        BeanValidatingItemProcessor<User> itemProcessor = new BeanValidatingItemProcessor();
        itemProcessor.setFilter(true);  //如果不满足数据直接抛弃
        return  itemProcessor;
    }

    //name转换大写操作
    @Bean
    public UserServiceImpl userService(){
        return new UserServiceImpl();
    }

    //处理逻辑
    @Bean
    public ItemProcessorAdapter<User, User> itemProcessorAdapter(){
        ItemProcessorAdapter<User, User> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetMethod("toUppeCase");  //将要调用的适配器指定的方法
        adapter.setTargetObject(userService());   //找到要适配 逻辑类：
        return adapter;

    }

    //组合
    @Bean
    public CompositeItemProcessor<User, User> compositeItemProcessor(){
        CompositeItemProcessor compositeItemProcessor = new CompositeItemProcessor();
        //组合多个处理器： 参数校验处理器  适配器处理器
        compositeItemProcessor.setDelegates(Arrays.asList(beanValidatingItemProcessor(), itemProcessorAdapter()));
        return compositeItemProcessor;
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
                .processor(compositeItemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("composite-processor-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CompositeProcessorJob.class, args);
    }
}
