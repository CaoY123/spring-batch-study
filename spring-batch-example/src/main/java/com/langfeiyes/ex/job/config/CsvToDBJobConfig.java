package com.langfeiyes.ex.job.config;


import com.langfeiyes.ex.domain.Employee;
import com.langfeiyes.ex.job.listener.CsvToDBJobListener;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.io.File;


//定义作业异步读employee.csv文件到数据库employee_temp
@Configuration
public class CsvToDBJobConfig {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value("${job.data.path}")
    private String path;

    @Autowired
    private SqlSessionFactory sqlSessionFactory; // sqlSessionFactory 底层封装了数据源


    //定义itemReader--多线程异步读
    @Bean
    public FlatFileItemReader<Employee> csvToDBitemReader(){
        return  new FlatFileItemReaderBuilder<Employee>()
                .name("employeeItemReader")
                .saveState(false)
                //读数据
                .resource(new PathResource(new File(path, "employee.csv").getAbsolutePath()))
                .delimited()
                .names("id", "name", "age", "sex")
                .targetType(Employee.class)
                .build();
    }

    //定义itemWriter --mybatis---
    @Bean
    public MyBatisBatchItemWriter<Employee> csvToDBItemWriter(){
        MyBatisBatchItemWriter<Employee> itemWriter = new MyBatisBatchItemWriter();
        //可以构建sqlsession类似jdbc connettion--可以执行sql语句类
        itemWriter.setSqlSessionFactory(sqlSessionFactory);
        //指定要执行save： Employee_temp 操作sql语句， 从EmployeeMapper.xml找id为saveTemp sql语句
        itemWriter.setStatementId("com.langfeiyes.ex.mapper.EmployeeMapper.saveTemp");
        return itemWriter;
    }

    //定义step
    @Bean
    public Step csvToDBStep(){
        return stepBuilderFactory.get("csvToDBStep")
                .<Employee, Employee>chunk(10000) //一次读10000个， 需要读50趟
                .reader(csvToDBitemReader())
                .writer(csvToDBItemWriter())
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    //定义监听器
    @Bean
    public CsvToDBJobListener  csvToDBJobListener(){
        return new CsvToDBJobListener();
    }

    //定义job
    @Bean
    public Job csvToDBJob(){
        return jobBuilderFactory.get("csvToDB-step-job")
                .start(csvToDBStep())
                .incrementer(new RunIdIncrementer())
                .listener(csvToDBJobListener())
                .build();
    }
}
