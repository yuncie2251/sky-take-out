package com.sky.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.getSetmealByConditions(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveNewSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insertNewSetmeal(setmeal);
        Long id = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(id);});
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getSetmealById(Long id) {

        SetmealVO setmealVO = new SetmealVO();
        Setmeal setmeal = setmealMapper.getSetmealByOneId(id);
        BeanUtils.copyProperties(setmeal,setmealVO);

        List<SetmealDish> dishes = setmealDishMapper.getDishesById(id);
        setmealVO.setSetmealDishes(dishes);

        return setmealVO;
    }

    /**
     * 设置选定id的起售停售状态
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //关联停售菜品的套餐不能起售
        List<SetmealDish> dishesById = setmealDishMapper.getDishesById(id);
        for (SetmealDish setmealDish : dishesById) {
            Long dishId = setmealDish.getDishId();
            Dish dishByOneId = dishMapper.getDishByOneId(dishId);
            Integer status1 = dishByOneId.getStatus();
            if(status1 == StatusConstant.DISABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }

        }


        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);

        setmealMapper.updateSetmeal(setmeal);
    }


    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateSetmealWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateSetmeal(setmeal);

        //之后再修改更改后的关联菜品
        setmealDishMapper.deleteById(setmealDTO.getId());
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if(dishes != null && dishes.size() > 0){
            dishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmealDTO.getId());});
            setmealDishMapper.insertBatch(dishes);
        }

    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteSetmeals(List<Long> ids) {
        //1.还在起售的套餐不能删
        List<Integer> statusOfAll = setmealMapper.getStatusOfIds(ids);
        for (Integer s : statusOfAll) {
            if(s == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //2.删的时候不仅要删除setmeal表，还要删除setmeal_dish表
        setmealMapper.deleteByIds(ids);
        setmealDishMapper.deleteByIds(ids);
    }


    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }


}
