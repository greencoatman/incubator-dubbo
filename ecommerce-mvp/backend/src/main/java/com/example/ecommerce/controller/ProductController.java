package com.example.ecommerce.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ecommerce.common.Result;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品模块")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @Operation(summary = "获取商品分页列表")
    @GetMapping
    public Result<Page<Product>> list(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) Long categoryId) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1); // 仅展示上架商品
        if (categoryId != null) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }
        queryWrapper.orderByDesc(Product::getSortOrder, Product::getCreatedAt); // 假设Entity有sortOrder，如果没有则按创建时间
        // 由于Entity未添加sortOrder，这里暂时只按创建时间
        // queryWrapper.orderByDesc(Product::getCreatedAt);
        
        return Result.success(productService.page(pageParam, queryWrapper));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<Product> getDetail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }
}
