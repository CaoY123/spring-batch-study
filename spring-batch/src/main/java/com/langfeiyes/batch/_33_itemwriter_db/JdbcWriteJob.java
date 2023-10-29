package com.langfeiyes.batch._33_itemwriter_db;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

import javax.sql.DataSource;


//读user.txt文件封装user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class JdbcWriteJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;

    //控制台输出
    /*@Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
            }
        };
    }*/

    //输出到outUser.txt文件
    /*@Bean
    public FlatFileItemWriter<User> itemWriter(){
        return new FlatFileItemWriterBuilder<User>()
                .name("userFlatItemWriter")
                //输出位置
                .resource(new PathResource("c:/outUser.txt"))
                .formatted()  //要进行格式输出
                .format("id: %s,姓名：%s,年龄：%s")  //输出数据格式
                .names("id", "name", "age")
                .build();
    }*/

    //json对象的调度器
    /*@Bean
    public JacksonJsonObjectMarshaller<User> jsonObjectMarshaller(){
        return new JacksonJsonObjectMarshaller<>();
    }
    //输出到outUser.txt文件
    @Bean
    public JsonFileItemWriter<User> itemWriter(){
        return new JsonFileItemWriterBuilder<User>()
                .name("userJsonItemWriter")
                //输出位置
                .resource(new PathResource("c:/outUser.json"))
                //json对象调度器--将user对象缓存json格式，输出文档中
                .jsonObjectMarshaller(jsonObjectMarshaller())
                .build();
    }*/

    @Bean
    public UserPreStatementSetter userPreStatementSetter(){
        return new UserPreStatementSetter();
    }

    //数据库输出
    @Bean
    public JdbcBatchItemWriter<User> itemWriter(){
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("insert into user(id, name, age) values(?,?,?)")
                //设置sql中占位符参数
                .itemPreparedStatementSetter(userPreStatementSetter())
                .build();
    }

    @Bean
    public FlatFileItemReader<User> itemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userDBItemReader")
                //获取文件
                .resource(new ClassPathResource("user.txt"))
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
        return stepBuilderFactory.get("step11")
                .<User, User>chunk(1) //一次读多少数据
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("json-writer-job1")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(JdbcWriteJob.class, args);
    }
}
