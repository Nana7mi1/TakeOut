package com.nanami.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanami.reggie.entity.Dish;
import com.nanami.reggie.entity.User;
import com.nanami.reggie.mapper.DishMapper;
import com.nanami.reggie.mapper.UserMapper;
import com.nanami.reggie.service.DishService;
import com.nanami.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
