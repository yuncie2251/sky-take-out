package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "value (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void add(Category category);

    Page<Category> pageShow(CategoryPageQueryDTO categoryPageQueryDTO);

    void update(Category category);

    @Delete("delete from category where id = #{id}")
    void delCal(Long id);

    List<Category> getByType(Integer type);
}
