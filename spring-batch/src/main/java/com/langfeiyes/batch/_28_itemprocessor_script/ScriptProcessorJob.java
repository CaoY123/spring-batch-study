package com.langfeiyes.batch._28_itemprocessor_script;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.ScriptItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;


//读user.txt文件封装user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class ScriptProcessorJob {

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
                //获取文件
                .resource(new ClassPathResource("users-adapter.txt"))
                //解析数据--指定解析器使用# 分割--默认是 ，号
                .delimited().delimiter("#")
                //按照 # 截取数据之后， 数据怎么命名
                .names("id", "name", "age")
                //封装数据--将读取的数据封装到对象：User对象
                .targetType(User.class)
                .build();
    }

    @Bean
    public ScriptItemProcessor<User, User> scriptItemProcessor() {
        ScriptItemProcessor processor = new ScriptItemProcessor();
        //加载js文件， 执行js中转换逻辑
        processor.setScript(new ClassPathResource("userScript.js"));

        return processor;
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1) //一次读多少数据
                .reader(itemReader())
                .processor(scriptItemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("ascript-processor-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ScriptProcessorJob.class, args);
    }
}
