package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void add(CategoryDTO categoryDTO);

    PageResult pageShow(CategoryPageQueryDTO categoryPageQueryDTO);

    void startOrStop(Integer status, Long id);

    void edit(CategoryDTO categoryDTO);

    void delCal(Long id);

    List<Category> getByType(Integer type);
}
