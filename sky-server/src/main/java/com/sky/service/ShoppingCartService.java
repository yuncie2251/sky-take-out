package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    void addItem(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();

    void clean();

    void subItem(ShoppingCartDTO shoppingCartDTO);
}
