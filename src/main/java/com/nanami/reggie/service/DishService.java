package com.nanami.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nanami.reggie.dto.DishDto;
import com.nanami.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    public void save2db(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void update2db(DishDto dishDto);
}
