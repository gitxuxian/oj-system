package com.xu.xuoj.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.xu.xuoj.common.BaseResponse;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.common.ResultUtils;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.model.dto.game.GameAddDTO;
import com.xu.xuoj.model.dto.game.GameQueryDTO;
import com.xu.xuoj.model.dto.game.GameUpdateDTO;
import com.xu.xuoj.model.entity.Game;
import com.xu.xuoj.model.vo.GameVO;
import com.xu.xuoj.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
@Api(tags = "竞赛模块")
public class GameController {


    @Resource
    private GameService gameService;

    @PostMapping("/add")
    @SaCheckRole("admin")
    @ApiOperation("添加竞赛")
    public BaseResponse<Long> addGame(@RequestBody @Valid GameAddDTO gameAddDTO) {
        Game game = new Game();
        BeanUtils.copyProperties(gameAddDTO, game);
        game.setCreatorUserId(StpUtil.getLoginIdAsLong());
        boolean result = gameService.save(game);
        if (!result) {
            throw new RuntimeException("添加比赛失败");
        }
        return ResultUtils.success(game.getId());
    }

    @PostMapping("/delete")
    @SaCheckRole("admin")
    @ApiOperation("删除竞赛")
    public BaseResponse<Boolean> deleteGame(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("比赛id不合法");
        }
        boolean result = gameService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @SaCheckRole("admin")
    @ApiOperation("更新竞赛")
    public BaseResponse<Boolean> updateGame(@RequestBody @Valid GameUpdateDTO gameUpdateDTO) {
        Game game = new Game();
        BeanUtils.copyProperties(gameUpdateDTO, game);
        boolean result = gameService.updateById(game);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
        }
        return ResultUtils.success(result);
    }

    @GetMapping("/get")
    @SaCheckLogin
    @ApiOperation("根据id获取竞赛")
    public BaseResponse<GameVO> getGameById(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("比赛id不合法");
        }
        Game game = gameService.getById(id);
        if (game == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "竞赛不存在");
        }
        GameVO gameVO = new GameVO();
        BeanUtils.copyProperties(game, gameVO);
        return ResultUtils.success(gameVO);
    }

    @PostMapping("/list")
    @SaCheckLogin
    @ApiOperation("获取竞赛列表")
    public BaseResponse<List<GameVO>> listGames(@RequestBody GameQueryDTO gameQueryDTO) {
        List<Game> games = gameService.listGames(gameQueryDTO);
        if (games.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "竞赛不存在");
        }
        List<GameVO> gameVOs = games.stream().map(game -> {
            GameVO gameVO = new GameVO();
            BeanUtils.copyProperties(game, gameVO);
            return gameVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(gameVOs);
    }

    @PostMapping("/updateStatus")
    @SaCheckRole("admin")
    @ApiOperation("更新竞赛状态")
    public BaseResponse<Boolean> updateGameStatus(
        @RequestParam Long id,
        @RequestParam Integer status) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("比赛id不合法");
        }
        if (status == null || status < 0 || status > 3) {
            throw new IllegalArgumentException("比赛状态不合法");
        }
        Game game = new Game();
        game.setId(id);
        game.setStatus(status);
        boolean result = gameService.updateById(game);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新失败");
        }
        return ResultUtils.success(result);
    }
}