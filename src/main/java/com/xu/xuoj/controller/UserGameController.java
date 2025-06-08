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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/game")
@Api(tags = "用户比赛模块")
public class UserGameController {

    @Resource
    private UserGameService userGameService;

    @PostMapping("/join")
    @SaCheckLogin
    @ApiOperation("参加比赛")
    public BaseResponse<Boolean> joinGame(@RequestParam Long gameId) {
        boolean result = userGameService.joinGame(StpUtil.getLoginIdAsLong(), gameId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "参加比赛失败");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    @SaCheckLogin
    @ApiOperation("退出比赛")
    public BaseResponse<Boolean> quitGame(@RequestParam Long gameId) {
        boolean result = userGameService.quitGame(StpUtil.getLoginIdAsLong(), gameId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "退出比赛失败");
        }
        return ResultUtils.success(result);
    }

    @GetMapping("/status")
    @SaCheckLogin
    @ApiOperation("获取比赛状态")
    public BaseResponse<UserGameVO> getGameStatus(
        @RequestParam Long userId,
        @RequestParam Long gameId) {
        UserGame userGame = userGameService.getUserGameRecord(userId, gameId);
        if (userGame == null) {
            return ResultUtils.success(null);
        }
        UserGameVO userGameVO = new UserGameVO();
        BeanUtils.copyProperties(userGame, userGameVO);
        return ResultUtils.success(userGameVO);
    }

    @GetMapping("/check")
    @SaCheckLogin
    @ApiOperation("检查是否已参加比赛")
    public BaseResponse<Boolean> checkJoinStatus(@RequestParam Long gameId) {
        boolean hasJoined = userGameService.hasJoinedGame(StpUtil.getLoginIdAsLong(), gameId);
        return ResultUtils.success(hasJoined);
    }

    @GetMapping("/my-games")
    @SaCheckLogin
    @ApiOperation("获取我参加的比赛")
    public BaseResponse<List<UserGameVO>> getMyGames() {
        List<UserGame> userGames = userGameService.getUserGames(StpUtil.getLoginIdAsLong());
        List<UserGameVO> userGameVOs = userGames.stream().map(userGame -> {
            UserGameVO userGameVO = new UserGameVO();
            BeanUtils.copyProperties(userGame, userGameVO);
            return userGameVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userGameVOs);
    }

    @GetMapping("/participants")
    @SaCheckLogin
    @ApiOperation("获取比赛参赛者列表")
    public BaseResponse<List<UserGameVO>> getGameParticipants(@RequestParam Long gameId) {
        List<UserGame> participants = userGameService.getGameParticipants(gameId);
        List<UserGameVO> participantVOs = participants.stream().map(userGame -> {
            UserGameVO userGameVO = new UserGameVO();
            BeanUtils.copyProperties(userGame, userGameVO);
            return userGameVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(participantVOs);
    }

    @PostMapping("/updateStatus")
    @SaCheckLogin
    @ApiOperation("更新比赛状态")
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
