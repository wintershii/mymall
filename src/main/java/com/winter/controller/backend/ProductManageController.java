package com.winter.controller.backend;

import com.google.common.collect.Maps;
import com.winter.common.Const;
import com.winter.common.ResponseCode;
import com.winter.common.ServerResponse;
import com.winter.pojo.Product;
import com.winter.pojo.User;
import com.winter.service.IFileService;
import com.winter.service.IProductService;
import com.winter.service.IUserService;
import com.winter.util.PropertiesUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 保存产品信息
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "/save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    /**
     * 设置产品状态
     * @param session
     * @param productId
     * @param productStatus
     * @return
     */
    @RequestMapping(value = "/setSaleStatus.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer productStatus) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.setSaleStatus(productId,productStatus);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    /**
     * 获取产品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "/detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    /**
     * 获取产品列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.getProductList(pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    /**
     * 根据相关信息搜索产品信息
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/search.do")
    @ResponseBody
    public ServerResponse ProductSearch(HttpSession session, String productName, Integer productId,
                                        @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    @RequestMapping(value = "/upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file", required = false)
            MultipartFile file, HttpServletRequest request) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
//        if (user == null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
//        } else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }

    }
}
