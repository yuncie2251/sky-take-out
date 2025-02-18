package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {



    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void saveDish(Dish dish);

    Page<DishVO> GetDishByX(DishPageQueryDTO dishPageQueryDTO);

    List<Dish> getDishById(List<Long> ids);

    void deleteByIds(List<Long> ids);

    @Select("select * from dish where id = #{id}")
    Dish getDishByOneId(Long id);

    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);

    @Select("select * from dish where category_id = #{id}")
    List<Dish> getDishByCategoryId(Long categoryId);

    List<Dish> list(Dish dish);
}
