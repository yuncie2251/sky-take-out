package com.sky.service.impl;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

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
}
