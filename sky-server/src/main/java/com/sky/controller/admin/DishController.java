package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关操作")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}....",dishDTO);
        dishService.save(dishDTO);

        return Result.success();

    }


    /**
     * 菜品分页展示
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页展示")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        //大概操作和员工、分类差不多，需要额外注意的点在于存在分类名，需要在回传对象时把数字换成对应的名字
        log.info("分页查询{}...",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品{}...",ids);
        dishService.deleteByIds(ids);
        return Result.success();
    }


    /**
     * 通过id查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "通过id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id){
        log.info("查询id为{}的菜品...",id);
        DishVO dish = dishService.getDishById(id);

        return Result.success(dish);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品{}....",dishDTO);
        dishService.updateDish(dishDTO);

        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getDishByCategoryId(Long categoryId){
        log.info("查询分类id为{}的菜品...",categoryId);
        List<Dish> dishes = dishService.getDishByCategoryId(categoryId);

        return Result.success(dishes);
    }
}