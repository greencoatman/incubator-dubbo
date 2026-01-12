<template>
  <view class="container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input class="search-input" placeholder="搜索商品" disabled />
    </view>

    <!-- 轮播图 (模拟) -->
    <swiper class="banner" indicator-dots autoplay circular>
      <swiper-item v-for="(item, index) in banners" :key="index">
        <image :src="item" mode="aspectFill" class="banner-image"></image>
      </swiper-item>
    </swiper>

    <!-- 分类入口 (模拟) -->
    <view class="category-grid">
      <view class="category-item" v-for="(cat, index) in categories" :key="index">
        <image :src="cat.icon" class="cat-icon"></image>
        <text class="cat-name">{{ cat.name }}</text>
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="product-list">
      <view class="section-title">热销推荐</view>
      <view class="product-grid">
        <view 
          class="product-item" 
          v-for="(item, index) in productList" 
          :key="item.id"
          @click="goToDetail(item.id)"
        >
          <image :src="item.mainImage" mode="aspectFill" class="prod-img"></image>
          <view class="prod-info">
            <text class="prod-title">{{ item.name }}</text>
            <view class="prod-price-row">
              <text class="price-symbol">¥</text>
              <text class="price-num">{{ item.price }}</text>
              <text class="sales">销量 {{ item.sales || 0 }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view class="loading-text" v-if="loading">加载中...</view>
    <view class="loading-text" v-else-if="finished">没有更多了</view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// 状态定义
const productList = ref([]);
const page = ref(1);
const loading = ref(false);
const finished = ref(false);

// 模拟数据
const banners = ref([
  'https://via.placeholder.com/750x300/1989fa/ffffff?text=Banner1',
  'https://via.placeholder.com/750x300/ff976a/ffffff?text=Banner2'
]);

const categories = ref([
  { name: '手机数码', icon: 'https://via.placeholder.com/100' },
  { name: '家用电器', icon: 'https://via.placeholder.com/100' },
  { name: '男装女装', icon: 'https://via.placeholder.com/100' },
  { name: '日用百货', icon: 'https://via.placeholder.com/100' },
]);

// 获取商品列表
const fetchProducts = async () => {
  if (loading.value || finished.value) return;
  loading.value = true;
  
  try {
    // 实际项目中替换为 uni.request
    // const res = await uni.request({
    //   url: 'http://localhost:8080/api/products',
    //   data: { page: page.value, size: 10 }
    // });
    
    // 模拟API响应延迟
    setTimeout(() => {
      const newProducts = Array.from({ length: 6 }).map((_, i) => ({
        id: Date.now() + i,
        name: `示例商品 ${page.value}-${i + 1}`,
        mainImage: 'https://via.placeholder.com/300',
        price: (Math.random() * 1000).toFixed(2),
        sales: Math.floor(Math.random() * 100)
      }));

      productList.value = [...productList.value, ...newProducts];
      page.value++;
      loading.value = false;
      
      // 模拟只有5页数据
      if (page.value > 5) finished.value = true;
    }, 500);
    
  } catch (e) {
    loading.value = false;
    uni.showToast({ title: '加载失败', icon: 'none' });
  }
};

// 页面加载
onMounted(() => {
  fetchProducts();
});

// 上拉加载更多 (需在pages.json配置)
const onReachBottom = () => {
  fetchProducts();
};

const goToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/product/detail?id=${id}`
  });
};

// 暴露给模板使用
defineExpose({
  onReachBottom
});
</script>

<style lang="scss">
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.search-bar {
  padding: 20rpx;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
  
  .search-input {
    background-color: #f7f8fa;
    height: 70rpx;
    border-radius: 35rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
  }
}

.banner {
  height: 300rpx;
  width: 100%;
  
  .banner-image {
    width: 100%;
    height: 100%;
  }
}

.category-grid {
  display: flex;
  justify-content: space-around;
  padding: 30rpx 0;
  background-color: #fff;
  margin-bottom: 20rpx;
  
  .category-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .cat-icon {
      width: 100rpx;
      height: 100rpx;
      border-radius: 50%;
      margin-bottom: 10rpx;
    }
    
    .cat-name {
      font-size: 24rpx;
      color: #333;
    }
  }
}

.product-list {
  padding: 0 20rpx;
  
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    margin: 20rpx 0;
    padding-left: 10rpx;
    border-left: 8rpx solid #ff5000;
  }
  
  .product-grid {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
  }
  
  .product-item {
    width: 48%;
    background-color: #fff;
    border-radius: 12rpx;
    overflow: hidden;
    margin-bottom: 20rpx;
    
    .prod-img {
      width: 100%;
      height: 340rpx;
    }
    
    .prod-info {
      padding: 16rpx;
      
      .prod-title {
        font-size: 28rpx;
        color: #333;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
        margin-bottom: 10rpx;
        height: 80rpx;
      }
      
      .prod-price-row {
        display: flex;
        align-items: baseline;
        
        .price-symbol {
          font-size: 24rpx;
          color: #ff5000;
        }
        
        .price-num {
          font-size: 36rpx;
          color: #ff5000;
          font-weight: bold;
          margin-right: 10rpx;
        }
        
        .sales {
          font-size: 22rpx;
          color: #999;
          margin-left: auto;
        }
      }
    }
  }
}

.loading-text {
  text-align: center;
  color: #999;
  font-size: 24rpx;
  padding: 20rpx 0;
}
</style>
