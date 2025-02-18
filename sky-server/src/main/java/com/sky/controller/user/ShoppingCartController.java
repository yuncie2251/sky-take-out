package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "购物车相关接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation(value = "购物车添加接口")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("向购物车中添加{}...",shoppingCartDTO);
        shoppingCartService.addItem(shoppingCartDTO);

        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation(value = "查看购物车")
    public Result<List<ShoppingCart>> list(){
        log.info("查看当前购物车...");
        List<ShoppingCart> shoppingCartList = shoppingCartService.list();

        return Result.success(shoppingCartList);

    }

    @DeleteMapping("/clean")
    @ApiOperation(value = "清空购物车")
    public Result clean(){
        log.info("清空购物车...");
        shoppingCartService.clean();

        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation(value = "删除购物车中一个商品")
    public Result subItem(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中一个商品{}...",shoppingCartDTO);
        shoppingCartService.subItem(shoppingCartDTO);

        return Result.success();
    }
}
