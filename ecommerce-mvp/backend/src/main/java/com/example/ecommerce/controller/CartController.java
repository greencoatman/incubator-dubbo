package com.example.ecommerce.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.common.Result;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.ICartService;
import com.example.ecommerce.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "购物车模块")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;
    private final IProductService productService;

    // 简单的购物车展示对象
    @Data
    public static class CartItemVO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private java.math.BigDecimal price;
        private Integer quantity;
        private Boolean selected;
    }

    @Operation(summary = "获取购物车列表")
    @GetMapping
    public Result<List<CartItemVO>> list(@RequestHeader(value = "X-User-ID", defaultValue = "1") Long userId) {
        // 模拟从Header获取当前登录用户ID
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId);
        List<CartItem> cartItems = cartService.list(queryWrapper);

        if (cartItems.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).collect(Collectors.toList());
        List<Product> products = productService.listByIds(productIds);
        
        // 转换为VO
        List<CartItemVO> vos = cartItems.stream().map(item -> {
            CartItemVO vo = new CartItemVO();
            vo.setId(item.getId());
            vo.setProductId(item.getProductId());
            vo.setQuantity(item.getQuantity());
            vo.setSelected(item.getSelected() == 1);
            
            products.stream()
                .filter(p -> p.getId().equals(item.getProductId()))
                .findFirst()
                .ifPresent(p -> {
                    vo.setProductName(p.getName());
                    vo.setProductImage(p.getMainImage());
                    vo.setPrice(p.getPrice());
                });
            return vo;
        }).collect(Collectors.toList());

        return Result.success(vos);
    }

    @Operation(summary = "添加到购物车")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody CartItem cartItem, @RequestHeader(value = "X-User-ID", defaultValue = "1") Long userId) {
        cartItem.setUserId(userId);
        
        // 检查是否已存在
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                    .eq(CartItem::getProductId, cartItem.getProductId());
        
        CartItem existingItem = cartService.getOne(queryWrapper);
        
        boolean success;
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            success = cartService.updateById(existingItem);
        } else {
            cartItem.setSelected(1);
            success = cartService.save(cartItem);
        }
        
        return Result.success(success);
    }

    @Operation(summary = "更新购物车数量")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody CartItem cartItem) {
        CartItem item = new CartItem();
        item.setId(id);
        item.setQuantity(cartItem.getQuantity());
        item.setSelected(cartItem.getSelected());
        return Result.success(cartService.updateById(item));
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(cartService.removeById(id));
    }
}
