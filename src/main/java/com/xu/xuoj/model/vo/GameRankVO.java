package com.xu.xuoj.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 游戏排行榜视图对象
 */
@Data
public class GameRankVO implements Serializable {
    
    /**
     * id
     */
    private Long id;

    /**
     * 竞赛id
     */
    private Long gameId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 总空间（kb）
     */
    private Integer totalMemory;

    /**
     * 总用时（ms）
     */
    private Integer totalTime;

    /**
     * 总得分
     */
    private Integer totalScore;

    /**
     * 竞赛详情
     */
    private String gameDetail;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
} 