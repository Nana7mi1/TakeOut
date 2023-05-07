package com.nanami.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nanami.reggie.dto.SetmealDto;
import com.nanami.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void deleteWithDish(List<Long> ids);
}
