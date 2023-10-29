package com.langfeiyes.ex.mapper;

import com.langfeiyes.ex.domain.Employee;

public interface EmployeeMapper {

    /**
     * 添加
     */
    int save(Employee employee);
    /**
     * 清空employee数据
     */
    void truncateAll();

    /**
     * 清空employee_temp数据
     */
    void truncateTemp();
}
