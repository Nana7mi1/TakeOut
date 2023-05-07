package com.nanami.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanami.reggie.common.CustomException;
import com.nanami.reggie.entity.Category;
import com.nanami.reggie.entity.Dish;
import com.nanami.reggie.entity.Setmeal;
import com.nanami.reggie.mapper.CategoryMapper;
import com.nanami.reggie.service.CategoryService;
import com.nanami.reggie.service.DishService;
import com.nanami.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if (count > 0) {
            //已经关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类已关联了菜品，不能删除该分类");
        }

        //查询当前分类是否已经关联了套餐，如果已经关联则抛出一个异常信息
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);

        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0) {
            //已经关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类已关联了套餐，不能删除该分类");
        }

        //正常删除分类
        super.removeById(id);
    }
}
