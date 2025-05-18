package com.xu.xuoj.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.xu.xuoj.common.BaseResponse;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.common.ResultUtils;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.entity.UserGame;
import com.xu.xuoj.model.vo.UserGameVO;
import com.xu.xuoj.service.UserGameService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user/game")
public class UserGameController {

    @Resource
    private UserGameService userGameService;

    @PostMapping("/join")
    @SaCheckLogin
    public BaseResponse<Boolean> joinGame(@RequestParam Long gameId) {
        boolean result = userGameService.joinGame(StpUtil.getLoginIdAsLong(), gameId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "参加比赛失败");
        }
        return ResultUtils.success(result);
    }

    @GetMapping("/status")
    @SaCheckLogin
    public BaseResponse<UserGameVO> getGameStatus(
        @RequestParam Long userId,
        @RequestParam Long gameId) {
        UserGame userGame = userGameService.getUserGameRecord(userId, gameId);
        UserGameVO userGameVO = new UserGameVO();
        BeanUtils.copyProperties(userGame, userGameVO);
        return ResultUtils.success(userGameVO);
    }

    @PostMapping("/updateStatus")
    @SaCheckLogin
    public BaseResponse<Boolean> updateGameStatus(
        @RequestParam Long gameId,
        @RequestParam Integer status) {
        boolean result = userGameService.updateGameStatus(StpUtil.getLoginIdAsLong(), gameId, status);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
        }
        return ResultUtils.success(result);
    }
}
