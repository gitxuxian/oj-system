package com.xu.xuoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xu.xuoj.model.entity.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {
    List<Question> selectGameQuestions(@Param("gameId") Long gameId);
}
