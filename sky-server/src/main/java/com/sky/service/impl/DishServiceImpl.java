package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 因为要向两张表中插入数据，所以使用事务处理
     * @param dishDTO
     */
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //首先先向菜品表中插入1条菜品信息
        dishMapper.saveDish(dish);

        //因为在保存口味的时候需要记录菜品id，所以需要在插入菜品信息后获取对应菜品id
        //此处要在对应mapper的xml文件中添加useGeneratedKey和KeyProperty
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() != 0){
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(id);});
            //其次向口味表中插入n个口味,批量操作
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    /**
     * 对菜品进行分页展示
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.GetDishByX(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 删除菜品操作
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {

        //首先，根据id找到dish表中对应的菜品
        List<Dish> dishes = dishMapper.getDishById(ids);


        //删除菜品前，需要进行如下考虑
        //1.菜品是否停售，若仍在启用，则无法删除
        for (Dish dish : dishes) {
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //2.菜品是否与套餐关联，若关联，则无法删除
        List<Long> setmealIds = setmealMapper.getSetmealById(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //判断完成后，不仅要在dish表中删除相应菜品
        dishMapper.deleteByIds(ids);

        //还应在dish-flavor表中删除对应菜品口味
        //因此，还应启用事务回滚
        dishFlavorMapper.deleteFlavorByDishId(ids);
    }

    @Override
    public DishVO getDishById(Long id) {
        //根据id获取菜品信息，不仅需要获取dish表中的dish信息，还要获取dish_flavor表中对应的flavor
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.getDishByOneId(id);
        BeanUtils.copyProperties(dish,dishVO);
        List<DishFlavor> flavors = dishFlavorMapper.getFlavorById(id);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    @Override
    public void updateDish(DishDTO dishDTO) {
        //在进行菜品修改时，需要额外注意的一点是口味的修改
        //与之前不同，菜品修改涉及到两张表的修改，原来的口味可能会删除/新增，比较保险的做法是先删掉之前的口味，然后再插入更新后的口味
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateDish(dish);

        Long id = dishDTO.getId();
        dishFlavorMapper.deleteFlavorByOneDishId(id);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() != 0){
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(id);});
            //其次向口味表中插入n个口味,批量操作
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    @Override
    public List<Dish> getDishByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.getDishByCategoryId(categoryId);
        return dishes;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getFlavorById(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
