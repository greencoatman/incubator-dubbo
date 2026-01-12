package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String detailContent;
    private Integer status;
    private Integer sales;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
