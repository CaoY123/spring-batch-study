package com.langfeiyes.batch._04_param_incr;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;


//以时间戳为参数增量器
public class DailyTimestampParamIncrementer  implements JobParametersIncrementer {
    @Override
    public JobParameters getNext(JobParameters parameters) {
        return new JobParametersBuilder(parameters)
                .addLong("daily", new Date().getTime())
                .toJobParameters();
    }
}
