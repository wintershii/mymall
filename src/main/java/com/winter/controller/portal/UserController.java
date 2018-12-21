package com.winter.controller.portal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public Object login(String username, String password, HttpSession session) {
        //service-->dao
        return null;
    }
}
