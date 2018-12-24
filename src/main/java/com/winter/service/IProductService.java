package com.winter.service;

import com.winter.common.ServerResponse;
import com.winter.pojo.Product;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
}
