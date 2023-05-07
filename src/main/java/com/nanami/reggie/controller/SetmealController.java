package com.nanami.reggie.controller;


import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlShowColumnOutpuVisitor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanami.reggie.common.Result;
import com.nanami.reggie.dto.SetmealDto;
import com.nanami.reggie.entity.Category;
import com.nanami.reggie.entity.Setmeal;
import com.nanami.reggie.service.CategoryService;
import com.nanami.reggie.service.SetmealDishService;
import com.nanami.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {


    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return Result.success("新增套餐成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        Page<Setmeal> page1 = new Page<>(page, pageSize);
        Page<SetmealDto> pageDto = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page1, queryWrapper);

        BeanUtils.copyProperties(page1, pageDto, "records");
        //设置records
        List<Setmeal> setmealList = page1.getRecords();
        List<SetmealDto> setmealDtoList = new ArrayList<>();

        for (Setmeal setmeal : setmealList) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            setmealDtoList.add(setmealDto);
        }
        pageDto.setRecords(setmealDtoList);

        return Result.success(pageDto);
    }


    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //删除所有缓存
    public Result<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteWithDish(ids);
        return Result.success("套餐删除成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, 1);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return Result.success(list);
    }
}
