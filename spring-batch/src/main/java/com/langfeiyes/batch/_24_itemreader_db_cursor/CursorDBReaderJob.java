package com.langfeiyes.batch._24_itemreader_db_cursor;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;

import javax.sql.DataSource;
import java.util.List;


//读user.txt文件封装user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class CursorDBReaderJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    //job--->step---tasklet
    //job--->step-chunk----reader---writer

    @Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
            }
        };
    }

    //将列数据与对象属性一一映射
    @Bean
    public UserRowMapper userRowMapper(){
        return new UserRowMapper();
    }

    //使用 jdbc 游标方式读数据
    @Bean
    public JdbcCursorItemReader<User> itemReader(){

        return new JdbcCursorItemReaderBuilder<User>()
                .name("userItemReader")
                //连接数据库， spring容器自己实现
                .dataSource(dataSource)
                //执行sql查询数据， 将返回的数据以游标形式一条一条读
                .sql("select * from user where age < ?")
                //拼接参数
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{16}))
                //数据库读出数据跟用户对象属性一一映射
                .rowMapper(userRowMapper())
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1) //一次读多少数据
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("cursor-db-reader-job1")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CursorDBReaderJob.class, args);
    }
}
