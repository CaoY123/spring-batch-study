package com.langfeiyes.batch._37_step_part;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//用户文件读取分区器
//作用： 指定从步骤名称 + 配置从步骤需要上下文环境
public class UserPartitioner implements Partitioner {
    int begin = 1;
    int end = 10;
    int range = 10;
    String text = "user%s-%s.txt";  //名字规则
    //map: key:从步骤名称  value： 从步骤上下文环境
    //gridSize：从步骤个数---当前是5
    //逻辑：
    //定义从步骤1---关联上下文环境---指定要处理文件名：file： user1-10.txt
    //定义从步骤2---关联上下文环境---指定要处理文件名：file：user11-20.txt
    //定义从步骤5---关联上下文环境---指定要处理文件名：file：user41-50.txt
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>();
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext ex = new ExecutionContext();
            String name = String.format(text, begin, end);
            Resource resource = new ClassPathResource(name);
            //springbatch转换成，单纯使用对象时报错，可以使用的url字符串形式， spring会自动加载
            try {
                ex.putString("file",resource.getURL().toExternalForm());
            } catch (IOException e) {
                e.printStackTrace();
            }
            begin += range;
            end += range;
            map.put("user_partition_" + i, ex);
        }
        return map;
    }
}
