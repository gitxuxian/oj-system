package com.xu.xuoj.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.xu.xuoj.common.BaseResponse;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.common.ResultUtils;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.entity.GameRank;
import com.xu.xuoj.model.vo.GameRankDetail;
import com.xu.xuoj.model.vo.GameRankVO;
import com.xu.xuoj.service.GameRankService;
import com.xu.xuoj.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game/rank")
@Api(tags = "比赛排行榜模块")
public class GameRankController {

    @Resource
    private GameRankService gameRankService;
    
    @Resource
    private GameService gameService;

    @GetMapping("/list")
    @SaCheckLogin
    @ApiOperation("获取比赛排行榜")
    public BaseResponse<List<GameRankVO>> getGameRankList(
            @RequestParam Long gameId,
            @RequestParam(defaultValue = "50") Integer limit) {
        // 检查是否可以查看排行榜
        if (!gameService.canViewRankings(gameId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "比赛未开始，无法查看排行榜");
        }
        
        // 自动更新比赛状态
        gameService.updateGameStatus(gameId);
        
        List<GameRank> gameRanks = gameRankService.getGameRankList(gameId, limit);
        List<GameRankVO> gameRankVOs = gameRanks.stream().map(gameRank -> {
            GameRankVO gameRankVO = new GameRankVO();
            BeanUtils.copyProperties(gameRank, gameRankVO);
            return gameRankVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(gameRankVOs);
    }

    @GetMapping("/user")
    @SaCheckLogin
    @ApiOperation("获取用户在比赛中的排名")
    public BaseResponse<GameRankVO> getUserRankInGame(
            @RequestParam Long gameId,
            @RequestParam Long userId) {
        GameRank gameRank = gameRankService.getUserRankInGame(gameId, userId);
        if (gameRank == null) {
            return ResultUtils.success(null);
        }
        GameRankVO gameRankVO = new GameRankVO();
        BeanUtils.copyProperties(gameRank, gameRankVO);
        return ResultUtils.success(gameRankVO);
    }

    @GetMapping("/detail")
    @SaCheckLogin
    @ApiOperation("获取比赛排行榜详情")
    public BaseResponse<List<GameRankDetail>> getGameRankDetail(@RequestParam Long gameId) {
        // 检查是否可以查看排行榜
        if (!gameService.canViewRankings(gameId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "比赛未开始，无法查看排行榜");
        }
        
        // 自动更新比赛状态
        gameService.updateGameStatus(gameId);
        
        List<GameRankDetail> gameRankDetails = gameRankService.getRankDetailByGameId(gameId);
        return ResultUtils.success(gameRankDetails);
    }

    @PostMapping("/calculate")
    @SaCheckLogin
    @ApiOperation("计算并更新排行榜")
    public BaseResponse<Boolean> calculateRankings(@RequestParam Long gameId) {
        boolean result = gameRankService.calculateRankings(gameId);
        return ResultUtils.success(result);
    }
} 