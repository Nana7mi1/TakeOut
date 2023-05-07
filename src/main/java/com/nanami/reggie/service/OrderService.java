package com.nanami.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nanami.reggie.entity.Orders;
import com.nanami.reggie.entity.User;
import com.sun.org.apache.xpath.internal.operations.Or;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
