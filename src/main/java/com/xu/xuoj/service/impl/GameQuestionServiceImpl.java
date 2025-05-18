package com.xu.xuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.mapper.GameQuestionMapper;
import com.xu.xuoj.mapper.QuestionMapper;
import com.xu.xuoj.model.dto.game.GameQuestionDTO;
import com.xu.xuoj.model.entity.GameQuestion;
import com.xu.xuoj.model.entity.Question;
import com.xu.xuoj.model.vo.QuestionVO;
import com.xu.xuoj.service.GameQuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameQuestionServiceImpl extends ServiceImpl<GameQuestionMapper, GameQuestion>
    implements GameQuestionService {

    @Resource
    private QuestionMapper questionMapper;

    @Override
    public boolean addQuestionToGame(GameQuestionDTO gameQuestionDTO) {
        Long questionId = gameQuestionDTO.getQuestionId();
        Long gameId = gameQuestionDTO.getGameId();
        // 校验题目是否存在
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        // 检查是否已关联
        GameQuestion exist = this.getOne(new QueryWrapper<GameQuestion>()
            .eq("gameId", gameId)
            .eq("questionId", questionId));
        if (exist != null) {
            throw new RuntimeException("题目已关联到该竞赛");
        }
        // 创建关联
        GameQuestion gameQuestion = new GameQuestion();
        BeanUtils.copyProperties(gameQuestionDTO, gameQuestion);
        return this.save(gameQuestion);
    }

    @Override
    public boolean removeQuestionFromGame(Long gameId, Long questionId) {
        return this.remove(new QueryWrapper<GameQuestion>()
            .eq("gameId", gameId)
            .eq("questionId", questionId));
    }

    @Override
    public List<QuestionVO> getGameQuestions(Long gameId) {
        List<Question> questions = questionMapper.selectGameQuestions(gameId);
        if (questions.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "没有题目");
        }
        List<QuestionVO> questionVOList = questions.stream().map(question -> {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(question, questionVO);
            return questionVO;
        }).filter(VO ->
            VO != null).collect(Collectors.toList());
        return questionVOList;
    }
}
