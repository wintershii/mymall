package com.winter.controller.backend;

import com.winter.common.ServerResponse;
import com.winter.pojo.User;
import com.winter.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;


    public ServerResponse<User> login(String username, String password, HttpSession session) {

    }

}