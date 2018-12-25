package com.winter.service.Impl;

import com.github.pagehelper.util.StringUtil;
import com.winter.common.ResponseCode;
import com.winter.common.ServerResponse;
import com.winter.dao.CategoryMapper;
import com.winter.dao.ProductMapper;
import com.winter.pojo.Category;
import com.winter.pojo.Product;
import com.winter.service.IProductService;
import com.winter.util.DateTimeUtil;
import com.winter.util.PropertiesUtil;
import com.winter.vo.ProductDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNoneBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }

            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            } else {
                int rowCount = productMapper.insert(product);;
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }


    public ServerResponse<String> setSaleStatus(Integer productId,Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    public ServerResponse<ProductDetail> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        //VO对象---value object
        //pojo --> bo(business object) -->  vo(view object)
        ProductDetail productDetail = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetail);
    }


    private ProductDetail assembleProductDetailVo(Product product) {
        ProductDetail productDetail = new ProductDetail();
        productDetail.setId(product.getId());
        productDetail.setCategoryId(product.getCategoryId());
        productDetail.setName(product.getName());
        productDetail.setMainImage(product.getMainImage());
        productDetail.setSubImages(product.getSubImages());
        productDetail.setStatus(product.getStatus());
        productDetail.setStock(product.getStock());
        productDetail.setDetail(product.getDetail());
        productDetail.setSubtitle(product.getSubtitle());
        productDetail.setPrice(product.getPrice());

        //imageHost
        //parentCategoryId
        //createTime
        //updateTime
        productDetail.setImageHost(PropertiesUtil.getProperty("",""));

        Category category = categoryMapper.selectByPrimaryKey(product.getId());
        if (category == null) {
            productDetail.setParentCategoryId(0);//默认根节点
        } else {
            productDetail.setParentCategoryId(category.getParentId());
        }

        productDetail.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetail.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetail;
    }
}
