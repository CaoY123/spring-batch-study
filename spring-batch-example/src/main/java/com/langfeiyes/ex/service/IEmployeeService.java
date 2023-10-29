package com.langfeiyes.ex.service;

import com.langfeiyes.ex.domain.Employee;

import java.io.IOException;

public interface IEmployeeService {
    /**
     * 保存
     */
    void save(Employee employee);

    /**
     * 实现50w员工数据初始化：
     * 动态生成50w条数据到employee.csv文件
     */
    void dataInit() throws IOException;

    /**
     * 清空employee数据
     */
    void truncateAll();

    /**
     * 清空employee_temp数据
     */
    void truncateTemp();

}
