package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public void addItem(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long user = BaseContext.getCurrentId();
        shoppingCart.setUserId(user);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectItem(shoppingCart);

        //首先判断要加入购物车的物品是否重复
        //如果购物车已存在该类物品，直接将数量加1即可
        if(shoppingCarts != null && shoppingCarts.size() > 0 ){
            ShoppingCart item = shoppingCarts.get(0);
            item.setNumber(item.getNumber() + 1);
            shoppingCartMapper.updateItemById(item);

        }else{
            if(shoppingCart.getSetmealId()!=null){
                Setmeal setmeal = setmealMapper.getSetmealByOneId(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }else{
                Dish dish = dishMapper.getDishByOneId(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }

            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartMapper.addItem(shoppingCart);
        }

    }

    @Override
    public List<ShoppingCart> list() {

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectItem(shoppingCart);

        return shoppingCartList;
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.cleanByUserId(userId);
    }

    @Override
    public void subItem(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectItem(shoppingCart);
        ShoppingCart item = shoppingCartList.get(0);
        int number = item.getNumber() - 1;
        if(number == 0){
            shoppingCartMapper.deleteById(item.getId());
        }else{
            item.setNumber(number);
            shoppingCartMapper.updateItemById(item);
        }



    }
}
