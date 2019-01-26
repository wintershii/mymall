package com.winter.controller.portal;

import com.winter.common.Const;
import com.winter.common.ResponseCode;
import com.winter.common.ServerResponse;
import com.winter.pojo.User;
import com.winter.service.ICartService;
import com.winter.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService cartService;

    @RequestMapping()
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.add(user.getId(),productId,count);
    }
}
