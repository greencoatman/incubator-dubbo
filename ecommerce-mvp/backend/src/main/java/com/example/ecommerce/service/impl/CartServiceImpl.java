package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.mapper.CartItemMapper;
import com.example.ecommerce.service.ICartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements ICartService {
}
