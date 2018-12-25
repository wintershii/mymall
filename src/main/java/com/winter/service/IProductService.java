package com.winter.service;

import com.winter.common.ServerResponse;
import com.winter.pojo.Product;
import com.winter.vo.ProductDetail;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    ServerResponse<ProductDetail> manageProductDetail(Integer productId)
}
