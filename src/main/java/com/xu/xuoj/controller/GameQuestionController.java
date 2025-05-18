package com.xu.xuoj.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.xu.xuoj.common.BaseResponse;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.common.ResultUtils;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.dto.game.GameQueryDTO;
import com.xu.xuoj.model.dto.game.GameQuestionDTO;
import com.xu.xuoj.model.entity.Question;
import com.xu.xuoj.model.vo.QuestionVO;
import com.xu.xuoj.service.GameQuestionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/game/question")
public class GameQuestionController {

    @Resource
    private GameQuestionService gameQuestionService;

    @PostMapping("/add")
    @SaCheckRole("admin")
    public BaseResponse<Boolean> addQuestionToGame(@RequestBody GameQuestionDTO gameQuestionDTO) {
        boolean result = gameQuestionService.addQuestionToGame(gameQuestionDTO);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加题目失败");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/remove")
    @SaCheckRole("admin")
    public BaseResponse<Boolean> removeQuestionFromGame(
        @RequestParam Long gameId,
        @RequestParam Long questionId) {
        boolean result = gameQuestionService.removeQuestionFromGame(gameId, questionId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "移除题目失败");
        }
        return ResultUtils.success(result);
    }

    @GetMapping("/list")
    @SaCheckLogin
    public BaseResponse<List<QuestionVO>> getGameQuestions(@RequestParam Long gameId) {
        List<QuestionVO> gameQuestions = gameQuestionService.getGameQuestions(gameId);
        if (gameQuestions.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该竞赛没有题目");
        }
        return ResultUtils.success(gameQuestions);
    }
}
