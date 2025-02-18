package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理相关操作")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询套餐")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询套餐{}...",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "seamealCache",key = "#setmealDTO.categoryId")
    public Result saveNewSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐{}...",setmealDTO);
        setmealService.saveNewSetmeal(setmealDTO);

        return Result.success();
    }


    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询套餐")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id){
        log.info("根据id{}查询套餐...",id);
        SetmealVO setmeal = setmealService.getSetmealById(id);

        return Result.success(setmeal);
    }


    /**
     * 套餐起售、停售
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "套餐起售、停售")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("设置id为{}的状态为{}...",id,status);
        setmealService.startOrStop(status,id);

        return Result.success();
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateSetmealWithDishes(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐{}...",setmealDTO);
        setmealService.updateSetmealWithDishes(setmealDTO);
        return Result.success();
    }


    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteSetmeals(@RequestParam List<Long> ids){
        log.info("删除套餐{}...",ids);
        setmealService.deleteSetmeals(ids);
        return Result.success();
    }
}
