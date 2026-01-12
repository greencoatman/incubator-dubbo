package com.example.ecommerce.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.common.Result;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.model.dto.CartAddDTO;
import com.example.ecommerce.model.vo.CartItemVO;
import com.example.ecommerce.service.ICartService;
import com.example.ecommerce.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Tag(name = "购物车模块")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final ICartService cartService;
    private final IProductService productService;

    @Operation(summary = "获取购物车列表")
    @GetMapping
    public Result<List<CartItemVO>> list(@RequestHeader(value = "X-User-ID", defaultValue = "1") Long userId) {
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId);
        List<CartItem> cartItems = cartService.list(queryWrapper);

        if (cartItems.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).collect(Collectors.toList());
        Map<Long, Product> productMap = productService.listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<CartItemVO> vos = cartItems.stream().map(item -> {
            CartItemVO vo = new CartItemVO();
            vo.setId(item.getId());
            vo.setProductId(item.getProductId());
            vo.setQuantity(item.getQuantity());
            vo.setSelected(item.getSelected() == 1);
            
            Product product = productMap.get(item.getProductId());
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setProductImage(product.getMainImage());
                vo.setPrice(product.getPrice());
                vo.setSubTotal(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
            return vo;
        }).collect(Collectors.toList());

        return Result.success(vos);
    }

    @Operation(summary = "添加到购物车")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody @Valid CartAddDTO cartDTO, 
                             @RequestHeader(value = "X-User-ID", defaultValue = "1") Long userId) {
        // 校验商品是否存在
        Product product = productService.getById(cartDTO.getProductId());
        if (product == null || product.getStatus() != 1) {
            return Result.error("商品不存在或已下架");
        }

        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                    .eq(CartItem::getProductId, cartDTO.getProductId());
        
        CartItem existingItem = cartService.getOne(queryWrapper);
        
        boolean success;
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartDTO.getQuantity());
            success = cartService.updateById(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(cartDTO.getProductId());
            newItem.setQuantity(cartDTO.getQuantity());
            newItem.setSelected(1);
            success = cartService.save(newItem);
        }
        
        return Result.success(success);
    }

    @Operation(summary = "更新购物车数量")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody CartItem cartItem) {
        // 实际上也应该使用 DTO，这里简化处理
        CartItem item = new CartItem();
        item.setId(id);
        if (cartItem.getQuantity() != null) item.setQuantity(cartItem.getQuantity());
        if (cartItem.getSelected() != null) item.setSelected(cartItem.getSelected());
        return Result.success(cartService.updateById(item));
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(cartService.removeById(id));
    }
}
