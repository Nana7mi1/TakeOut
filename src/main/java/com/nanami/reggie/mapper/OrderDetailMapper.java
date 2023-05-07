package com.nanami.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nanami.reggie.entity.OrderDetail;
import com.nanami.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
