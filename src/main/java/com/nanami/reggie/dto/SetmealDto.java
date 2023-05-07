package com.nanami.reggie.dto;

import com.nanami.reggie.entity.Setmeal;
import com.nanami.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
