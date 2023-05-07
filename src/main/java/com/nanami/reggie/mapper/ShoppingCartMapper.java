package com.nanami.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nanami.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}