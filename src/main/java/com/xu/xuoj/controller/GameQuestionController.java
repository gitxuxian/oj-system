package com.xu.xuoj.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.xu.xuoj.common.BaseResponse;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.common.ResultUtils;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.dto.game.GameQueryDTO;
import com.xu.xuoj.model.dto.game.GameQuestionAddRequest;
import com.xu.xuoj.model.dto.game.GameQuestionDTO;
import com.xu.xuoj.model.entity.Question;
import com.xu.xuoj.model.vo.QuestionVO;
import com.xu.xuoj.service.GameQuestionService;
import com.xu.xuoj.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/game/question")
@Api(tags = "竞赛题目模块")
public class GameQuestionController {

    @Resource
    private GameService gameService;

    @Resource
    private GameQuestionService gameQuestionService;

    @PostMapping("/add")
    @SaCheckRole("admin")
    @ApiOperation("添加竞赛题目")
    public BaseResponse<Boolean> addQuestionToGame(@RequestBody GameQuestionDTO gameQuestionDTO) {
        boolean result = gameQuestionService.addQuestionToGame(gameQuestionDTO);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加题目失败");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/remove")
    @SaCheckRole("admin")
    @ApiOperation("移除竞赛题目")
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
    @ApiOperation("查询竞赛题目")
    public BaseResponse<List<QuestionVO>> getGameQuestions(@RequestParam Long gameId) {
        List<QuestionVO> gameQuestions = gameQuestionService.getGameQuestions(gameId);
        if (gameQuestions.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该竞赛没有题目");
        }
        return ResultUtils.success(gameQuestions);
    }

    @PostMapping("/game_submit")
    @ApiOperation("竞赛提交")
    public BaseResponse<Long> questionSubmit(@RequestBody GameQuestionAddRequest gameQuestionAddRequest) {
        if (gameQuestionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return null;
    }

}
