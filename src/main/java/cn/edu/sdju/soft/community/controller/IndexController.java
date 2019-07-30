package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.mapper.UserMapper;
import cn.edu.sdju.soft.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/")
    public String index(HttpServletRequest request){
        /*service层需要抛出异常，只用controller的话可能同一种异常多处try-catch*/
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie:cookies) {
                if ("token".equals(cookie.getName())){
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if (user != null){
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        }

        return "index";
    }

}
