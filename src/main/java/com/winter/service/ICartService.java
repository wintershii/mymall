package com.winter.service;

import com.winter.common.ServerResponse;
import com.winter.vo.CartVo;

public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);
    ServerResponse<CartVo> list(Integer userId);
    ServerResponse<CartVo> selectAllUnSelect(Integer userId, Integer checked, Integer productId);
    ServerResponse<Integer> getCartProductCount(Integer userId);
}
