package com.langfeiyes.ex.service.impl;

import com.langfeiyes.ex.domain.Employee;
import com.langfeiyes.ex.mapper.EmployeeMapper;
import com.langfeiyes.ex.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public void save(Employee employee) {
        employeeMapper.save(employee);
    }

    @Value("${job.data.path}")
    private String path;

    @Override
    public void dataInit() throws IOException {

        //1: 创建输出文件
        File file = new File(path, "employee.csv");
        if(file.exists()){
            file.delete();
        }
        file.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(file);
        //输出的数据

        //2：创建50w条数据
        Random ageR = new Random();
        Random sexR = new Random();
        String txt = "";

        long begin = System.currentTimeMillis();
        //数据格式： id,用户名,年龄,性别
        for (int i = 1; i <= 500000 ; i++) {
            if(i == 500000){
                txt = i + "," + "dafei_" + i + "," + ageR.nextInt(100) + "," + (sexR.nextBoolean()?1:0);
            }else{
                txt = i + "," + "dafei_" + i + "," + ageR.nextInt(100) + "," + (sexR.nextBoolean()?1:0) + "\n";
            }
            //3:数据输出
            outputStream.write(txt.getBytes());
            outputStream.flush();
        }
        long end = System.currentTimeMillis();
        System.out.println("消耗时间：" + (end - begin) + " 毫秒");

        outputStream.close();
    }


    @Override
    public void truncateAll() {
        employeeMapper.truncateAll();
    }

    @Override
    public void truncateTemp() {
        employeeMapper.truncateTemp();
    }
}