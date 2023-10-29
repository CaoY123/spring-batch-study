package com.langfeiyes.ex.job.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;


//db2db 分区器设置， 将从步骤需要的from， to range 3个设置到从步骤中上下文中
public class DBToDBPartitioner  implements Partitioner {
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        String text = "----DBToDBPartitioner---第%s分区-----开始：%s---结束：%s---数据量：%s--------------";
        Map<String, ExecutionContext> map = new HashMap<>();
        int from = 1;
        int to = 10000;
        int range = 10000;

        for (int i = 0; i < gridSize; i++) {
            System.out.println(String.format(text, i, from, to, (to - from + 1)));

            ExecutionContext ex = new ExecutionContext();
            ex.putInt("from", from);
            ex.putInt("to", to);
            ex.putInt("range", range);

            to += range;
            from += range;

            map.put("partitioner_" + i, ex);
        }
        return map;
    }
}
