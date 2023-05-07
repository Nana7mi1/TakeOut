package com.nanami.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanami.reggie.common.Result;
import com.nanami.reggie.dto.DishDto;
import com.nanami.reggie.entity.Category;
import com.nanami.reggie.entity.Dish;
import com.nanami.reggie.entity.DishFlavor;
import com.nanami.reggie.entity.Employee;
import com.nanami.reggie.service.CategoryService;
import com.nanami.reggie.service.DishFlavorService;
import com.nanami.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> add(@RequestBody DishDto dishDto){
        dishService.save2db(dishDto);
        return Result.success("新增菜品成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        // 构造分页器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        // 构造dto分页器对象,Dish实体类缺少categoryName和flavors字段，故需要声明DishDto类进行字段扩展
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 添加条件过滤
        queryWrapper.like(name != null,Dish::getName,name);

        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 执行分页查询,查询数据并赋值到pageInfo
        dishService.page(pageInfo,queryWrapper);

        // 对象拷贝, 其中records就是最终返回给页面的数据，由于我们需要对其进行处理，所以不对该字段进行拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        // 获取pageInfo中的records数据
        List<Dish> records = pageInfo.getRecords();


        //太麻烦了，效率低，不如Mybatis
        List<DishDto> list = records.stream().map((item) -> {

            // 定义一个临时变量dto
            DishDto dishDto = new DishDto();

            // 将当前处理的项拷贝到临时变量
            BeanUtils.copyProperties(item,dishDto);

            // 根据每一项的categoryId查询到对应的category
            Long categoryId = item.getCategoryId(); //分类id

            // 根据分类id查询对象
            Category category = categoryService.getById(categoryId);

            if(category!=null){
                String categoryName = category.getName();
                // 对dishDto中的categoryName赋值
                dishDto.setCategoryName(categoryName);
            }


            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return Result.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public Result<DishDto> getById(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return Result.success(dishDto);
    }


    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        dishService.update2db(dishDto);
        return Result.success("修改菜品成功");
    }

    /*
    @GetMapping("/list")
    public Result<List<Dish>> listDish(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1); // 起售状态
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(queryWrapper);
        return Result.success(dishes);
    }
     */
    @GetMapping("/list")
    public Result<List<DishDto>> listDish(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1); // 起售状态
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(queryWrapper);

        List<DishDto> list = dishes.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
//            Long categoryId = item.getCategoryId(); //分类id
//            Category category = categoryService.getById(categoryId);
//            String categoryName = category.getName();
//            dishDto.setCategoryName(categoryName);
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, item.getId());
            dishDto.setFlavors(dishFlavorService.list(queryWrapper1));
            return dishDto;
        }).collect(Collectors.toList());


        return Result.success(list);
    }
}
