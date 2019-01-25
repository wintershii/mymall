package com.winter.service;

import com.github.pagehelper.PageInfo;
import com.winter.common.ServerResponse;
import com.winter.pojo.Product;
import com.winter.vo.ProductDetail;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    ServerResponse<ProductDetail> manageProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);
    ServerResponse<PageInfo> searchProduct(String productName,Integer productId, int pageNum,int pageSize);
    ServerResponse<ProductDetail> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum,
                                                         int pageSize,String orderBy);
}
