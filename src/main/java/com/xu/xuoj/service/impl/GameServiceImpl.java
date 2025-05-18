package com.xu.xuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.xuoj.mapper.GameMapper;

import com.xu.xuoj.model.dto.game.GameQueryDTO;
import com.xu.xuoj.model.entity.Game;
import com.xu.xuoj.service.GameService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hy
 * @description 针对表【game(比赛信息表)】的数据库操作Service实现
 * @createDate 2025-05-17 14:51:46
 */
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game>
    implements GameService {

    @Override
    public List<Game> listGames(GameQueryDTO gameQueryDTO) {
        QueryWrapper<Game> queryWrapper = new QueryWrapper<>();
        if (gameQueryDTO == null) {
            return this.list(queryWrapper);
        }

        // 处理查询条件
        if (StringUtils.isNotBlank(gameQueryDTO.getGameName())) {
            queryWrapper.like("game_name", gameQueryDTO.getGameName());
        }
        if (gameQueryDTO.getType() != null) {
            queryWrapper.eq("type", gameQueryDTO.getType());
        }
        if (gameQueryDTO.getStatus() != null) {
            queryWrapper.eq("status", gameQueryDTO.getStatus());
        }
        if (gameQueryDTO.getGameDateStart() != null && gameQueryDTO.getGameDateEnd() != null) {
            queryWrapper.between("game_date", gameQueryDTO.getGameDateStart(), gameQueryDTO.getGameDateEnd());
        }

        // 处理分页
        if (gameQueryDTO.getPageSize() != 0 && gameQueryDTO.getCurrent() != 0) {
            Page<Game> page = new Page<>(gameQueryDTO.getCurrent(), gameQueryDTO.getPageSize());
            return this.page(page, queryWrapper).getRecords();
        }
        return this.list(queryWrapper);
    }
}

