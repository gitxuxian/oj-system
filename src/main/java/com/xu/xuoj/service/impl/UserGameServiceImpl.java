package com.xu.xuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.xuoj.mapper.UserGameMapper;
import com.xu.xuoj.model.entity.UserGame;
import com.xu.xuoj.service.UserGameService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserGameServiceImpl extends ServiceImpl<UserGameMapper, UserGame>
    implements UserGameService {

    @Override
    public boolean joinGame(Long userId, Long gameId) {
        // 检查是否已报名
        UserGame exist = this.getOne(new QueryWrapper<UserGame>()
            .eq("userId", userId)
            .eq("gameId", gameId));
        if (exist != null) {
            throw new RuntimeException("您已报名该竞赛");
        }

        // 创建参赛记录
        UserGame userGame = new UserGame();
        userGame.setUserId(userId);
        userGame.setGameId(gameId);
        userGame.setRegistrationTime(new Date());
        userGame.setStatus(0); // 0表示已报名
        userGame.setIsOfficial(1); // 1表示正式参赛者
        return this.save(userGame);
    }

    @Override
    public UserGame getUserGameRecord(Long userId, Long gameId) {
        return this.getOne(new QueryWrapper<UserGame>()
            .eq("userId", userId)
            .eq("gameId", gameId));
    }

    @Override
    public boolean updateGameStatus(Long userId, Long gameId, Integer status) {
        UserGame userGame = new UserGame();
        userGame.setStatus(status);
        return this.update(userGame, new QueryWrapper<UserGame>()
            .eq("userId", userId)
            .eq("gameId", gameId));
    }
}
