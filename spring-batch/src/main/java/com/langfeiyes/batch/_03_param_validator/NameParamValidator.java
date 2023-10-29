package com.langfeiyes.batch._03_param_validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

/**
 * 进行name参数校验：
 *  规则： 当name值为null 或者 空串 "" 校验不通过， 抛出异常
 */
public class NameParamValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String name = parameters.getString("name");
        if (!StringUtils.hasText(name)){
            throw new JobParametersInvalidException("name 参数值不能为null 或者 空串");
        }

    }
}
