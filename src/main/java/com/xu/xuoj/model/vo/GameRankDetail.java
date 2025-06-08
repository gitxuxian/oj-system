package com.xu.xuoj.model.vo;

import com.xu.xuoj.model.entity.GameDetailUnit;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 比赛排行榜详情VO
 * @author xu
 */
@Data
public class GameRankDetail implements Serializable {
    
    /**
     * 名次
     */
    private Integer rankOrder;

    /**
     * 答题者id
     */
    private Long userId;

    /**
     * 答题者昵称
     */
    private String userName;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 总耗时（ms）
     */
    private Integer totalTime;

    /**
     * 总耗用内存（kb）
     */
    private Integer totalMemory;

    /**
     * 最优答题情况集合
     */
    private List<GameDetailUnit> questionDetails;

    private static final long serialVersionUID = 1L;
} 