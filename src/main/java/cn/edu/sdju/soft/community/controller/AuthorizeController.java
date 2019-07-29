package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.dto.AccessTokenDTO;
import cn.edu.sdju.soft.community.dto.GithubUser;
import cn.edu.sdju.soft.community.mapper.UserMapper;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")//会去配置文件里读
    private String clientId;

    @Value("{github.client.secret}")
    private String clientSecret;

    @Value("{github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        /*shift+F6替换所有user*/
        GithubUser githubUser = githubProvider.getUser(accessToken);
        /*当使用github登录成功时，获取用户信息，生成一个token，把token放入到用户对象里去，存储到数据库中，并且把token放到数据库里去*/
        if(githubUser != null){
            User user = new User();
            /*shift+回车直接切换到下一行*/
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            /*Ctrl+Alt+v抽取变量*/
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
            //登录成功，写cookie对象和session
        }else{
            return "redirect:/";
            //登录失败，重新登录
        }
    }
}
