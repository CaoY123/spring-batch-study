package com.langfeiyes.ex.job.config;

import com.langfeiyes.ex.domain.Employee;
import com.langfeiyes.ex.job.partitioner.DBToDBPartitioner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置分区作业
 * 将数据从employee_temp中读取，并写入employe 表
 */
@Configuration
public class DBToDBJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    //从步骤读数据--reader
    //存在问题： 有50从步骤， 50w条数据， 怎么分配
    //从步骤1： select * from employee_temp where id between 1 and 10000 limit offset, pageSize
    //从步骤2： select * from employee_temp where id between 10001 and 20000 limit offset, pageSize
    //从步骤50： select * from employee_temp where id between 490001 and 500000 limit offset, pageSize
    //reader组件定义必须指定操作数据： from  to  pageSize（pageSize 确定后 offset 会自己算出来）

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Employee> dbToDBItemReader(
            @Value("#{stepExecutionContext[from]}")Integer from,
            @Value("#{stepExecutionContext[to]}")Integer to,
            @Value("#{stepExecutionContext[range]}")Integer range){
        System.out.println("----------MyBatisPagingItemReader开始-----from: " + from + "  -----to:" + to + "  -----每片数量:" + range);
        MyBatisPagingItemReader<Employee> itemReader = new MyBatisPagingItemReader<>();
        itemReader.setSqlSessionFactory(sqlSessionFactory);
        itemReader.setPageSize(1000);  //约定每页显示1000条
        itemReader.setQueryId("com.langfeiyes.ex.mapper.EmployeeMapper.selectTempForList");
        //key： sql中条件参数， value： 条件值
        Map<String, Object> map = new HashMap<>();
        map.put("from", from);
        map.put("to", to);
        itemReader.setParameterValues(map);
        //from  // to  在 分区器中配置， 当前可以重上下文中获取
        return itemReader;

    }


    //从步骤写数据--writer
    @Bean
    public MyBatisBatchItemWriter<Employee> dbToDBItemWriter(){
        MyBatisBatchItemWriter<Employee> itemWriter = new MyBatisBatchItemWriter();
        //可以构建sqlsession类似jdbc connettion--可以执行sql语句类
        itemWriter.setSqlSessionFactory(sqlSessionFactory);
        //指定要执行save： Employee_temp 操作sql语句， 从EmployeeMapper.xml找id为saveTemp sql语句
        itemWriter.setStatementId("com.langfeiyes.ex.mapper.EmployeeMapper.save");
        return itemWriter;
    }

    //从步骤
    @Bean
    public Step workStep(){
        return stepBuilderFactory.get("workStep")
                .<Employee,Employee>chunk(1000)
                .reader(dbToDBItemReader(null, null,null))
                .writer(dbToDBItemWriter())
                .build();
    }

    //分区器
    @Bean
    public DBToDBPartitioner dbToDBPartitioner(){
        return new DBToDBPartitioner();
    }

    //分区处理器
    @Bean
    public PartitionHandler dbToDBPartitionHandler(){
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();

        handler.setGridSize(50);
        handler.setTaskExecutor(new SimpleAsyncTaskExecutor());
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
                .partitioner(workStep().getName(), dbToDBPartitioner())
                .partitionHandler(dbToDBPartitionHandler())
                .build();
    }

    //作业
    @Bean
    public Job dbToDBJob(){
        return jobBuilderFactory.get("dbToDB-step-job")
                .start(masterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
