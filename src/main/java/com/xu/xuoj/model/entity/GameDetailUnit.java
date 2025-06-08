package com.xu.xuoj.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 比赛详情单元实体类
 * @author xu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDetailUnit implements Serializable
{
    /**
     * 单题id
     */
    private Long id;

    /**
     * 单题名称
     */
    private String name;

    /**
     * 单题得分
     */
    private Integer score;

    /**
     * 单题耗时（ms）
     */
    private Integer timeCost;

    /**
     * 单题耗内存（kb）
     */
    private Integer memoryCost;

    /**
     * 比较是否比另一个好
     * 判断标准：分数->耗时->内存
     * @param other 另一个答题结果
     * @return 是否更好
     */
    public boolean isBetter(GameDetailUnit other)
    {
        if (other == null)
        {
            return true;
        }
        
        // 首先比较分数是否更大
        if (this.score > other.getScore())
        {
            return true;
        }
        if (this.score < other.getScore())
        {
            return false;
        }
        
        // 分数相同，比较耗时是否更少
        if (this.timeCost < other.getTimeCost())
        {
            return true;
        }
        if (this.timeCost > other.getTimeCost())
        {
            return false;
        }
        
        // 耗时相同，比较耗费空间是否更少
        return this.memoryCost < other.getMemoryCost();
    }

    private static final long serialVersionUID = 1L;
}

