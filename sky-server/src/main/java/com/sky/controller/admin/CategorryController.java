package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理相关操作")
public class CategorryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增分类")
    public Result add(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类{}",categoryDTO);
        categoryService.add(categoryDTO);

        return Result.success();
    }


    /**
     * 分类分页操作
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分类分页操作")
    public Result<PageResult> pageShow(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页操作{}",categoryPageQueryDTO);
        PageResult result = categoryService.pageShow(categoryPageQueryDTO);

        return Result.success(result);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用分类")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("将id为{}的分类设置status为{}",id,status);
        categoryService.startOrStop(status,id);

        return Result.success();
    }


    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改分类")
    public Result edit(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类信息{}",categoryDTO);
        categoryService.edit(categoryDTO);

        return Result.success();
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "根据id删除分类")
    public Result delCal(Long id){
        log.info("删除id为{}的分类",id);
        categoryService.delCal(id);

        return Result.success();
    }


    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List> getByType(Integer type){
        log.info("查询类型为{}的分类",type);
        List<Category> list = categoryService.getByType(type);

        return Result.success(list);
    }

}
