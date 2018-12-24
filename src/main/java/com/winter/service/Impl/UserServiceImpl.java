package com.winter.service.Impl;

import com.winter.common.Const;
import com.winter.common.ServerResponse;
import com.winter.common.TokenCache;
import com.winter.dao.UserMapper;
import com.winter.pojo.User;
import com.winter.service.IUserService;
import com.winter.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        // 密码登录MD5

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);

    }

    public ServerResponse<String> register(User user) {

        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }


    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNoneBlank(type)) {
            //开始校验
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }

            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccess("校验成功");
    }

    public ServerResponse selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNoneBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题为空");
    }


    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount > 0) {
            //说明问题及问题答案是这个用户的 且答案正确
            String forgetToken = UUID.randomUUID().toString();

            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if (StringUtils.equals(forgetToken,token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("修改密码成功");
            } else {
                return ServerResponse.createByErrorMessage("token错误,请重新获取token");
            }
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户
        //因为我们会查询到一个count(1)，如果不指定id，那么结果就是true count>0
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }


    public ServerResponse<User> updateInformation(User user) {
        //username 不能被更新
        //email也要进行校验 校验新的email是不是已经存在 如果相同的话，不能是当前用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("email已存在，请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    //backend

    /**
     * 校验当前用户是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


}
