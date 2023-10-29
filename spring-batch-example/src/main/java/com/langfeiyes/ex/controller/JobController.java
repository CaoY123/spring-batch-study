package com.langfeiyes.ex.controller;

import com.langfeiyes.ex.service.IEmployeeService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class JobController {

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("csvToDBJob")  //将spring容器中名为： csvToDBJob job对象注入当前变量中
    private Job csvToDBJob;

    @Autowired
    @Qualifier("dbToDBJob")  //将spring容器中名为： csvToDBJob job对象注入当前变量中
    private Job dbToDBJob;

    @Autowired
    private JobExplorer jobExplorer;

    @GetMapping("/csvToDB")
    public String csvToDB() throws Exception {
        //允许多次执行每次操作前，将employee_temp数据清空
        employeeService.truncateTemp();
        //启动作业--可以重复执行，记录操作时间戳
        JobParameters parameters = new JobParametersBuilder(new JobParameters(), jobExplorer)
                .addLong("time", new Date().getTime())
                .getNextJobParameters(csvToDBJob).toJobParameters();
        JobExecution run = jobLauncher.run(csvToDBJob, parameters);
        return run.getId().toString();
    }

    @GetMapping("/dbToDB")
    public String dbToDB() throws Exception {
        //允许多次执行每次操作前，
        employeeService.truncateAll();
        //启动作业--可以重复执行，记录操作时间戳
        JobParameters parameters = new JobParametersBuilder(new JobParameters(), jobExplorer)
                .addLong("time", new Date().getTime())
                .getNextJobParameters(dbToDBJob).toJobParameters();
        JobExecution run = jobLauncher.run(dbToDBJob, parameters);
        return run.getId().toString();
    }
}
