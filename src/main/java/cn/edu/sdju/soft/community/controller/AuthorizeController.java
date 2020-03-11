package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.dto.AccessTokenDTO;
import cn.edu.sdju.soft.community.dto.GithubUser;
import cn.edu.sdju.soft.community.dto.PaginationDTO;
import cn.edu.sdju.soft.community.model.CheckQuestions;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.model.UserCheckQuestion;
import cn.edu.sdju.soft.community.provider.GithubProvider;
import cn.edu.sdju.soft.community.service.CheckQuestionsService;
import cn.edu.sdju.soft.community.service.UserCheckQuestionService;
import cn.edu.sdju.soft.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * github授权登录：
 * 1.点击登录，跳转到github，authorize授权登陆
 * 2.github回调redirect-uri 携带code、client_id、status、scope到登录页面（执行callback的controller）
 * 3.access_token携带code到github（getAccessToken()方法里的request请求）
 * 4.github返回access_token到登录页面（getAccessToken()方法里的response响应获得）
 * 5.user携带access_token到github（getUser()方法里的request请求）
 * 6.github返回user信息(getUser()方法里的response响应获得)
 */
@Controller
@Slf4j
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")//会去配置文件里读
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @Autowired
    private CheckQuestionsService checkQuestionsService;

    @Autowired
    private UserCheckQuestionService userCheckQuestionService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        System.out.println(accessToken);
        /*shift+F6替换所有user*/
        GithubUser githubUser = githubProvider.getUser(accessToken);
        /*当使用github登录成功时，获取用户信息，生成一个token，把token放入到用户对象里去，存储到数据库中，并且把token放到数据库里去*/
        if (githubUser != null && githubUser.getId() != null) {
            User user = new User();
            /*shift+回车直接切换到下一行*/
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatar_url());
            /*Ctrl+Alt+v抽取变量*/
            userService.createOrUpdate(user);
            response.addCookie(new Cookie("token", token));
            return "redirect:/";
            //登录成功，写cookie对象和session
        } else {
            log.error("callback get github error,{}", githubUser);
            return "redirect:/";
            //登录失败，重新登录
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        /*session.removeAttribute("user");
        session.invalidate();*/
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("user", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
            /*HttpSession session,*/
                        HttpServletResponse response,
                        Model model) {
        User user = userService.findByUsername(username, password);
        if (user == null) {
            model.addAttribute("error", "账号或密码错误，请重新登录！");
            return "login";
        } else if (user.getState() == 0){
            model.addAttribute("error","您的账户已被冻结！");
            return "login";
        }else {
            /*session.setAttribute("user",user);*/
            response.addCookie(new Cookie("user", username));
            return "redirect:/";
        }

    }

    @GetMapping("/toRegister")
    public String toRegister(Model model) {
        List<CheckQuestions> checkQuestionsList1 = checkQuestionsService.findPartCheckQuestions1();
        List<CheckQuestions> checkQuestionsList2 =checkQuestionsService.findPartCheckQuestions2();
        List<CheckQuestions> checkQuestionsList3 =checkQuestionsService.findPartCheckQuestions3();
        model.addAttribute("checkQuestionsList1",checkQuestionsList1);
        model.addAttribute("checkQuestionsList2",checkQuestionsList2);
        model.addAttribute("checkQuestionsList3",checkQuestionsList3);
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("name") String name,
                           @RequestParam("email") String email,
                           @RequestParam("checkQuestion1")Long checkQuestion1,
                           @RequestParam("checkAnswer1")String checkAnswer1,
                           @RequestParam("checkQuestion2")Long checkQuestion2,
                           @RequestParam("checkAnswer2")String checkAnswer2,
                           @RequestParam("checkQuestion3")Long checkQuestion3,
                           @RequestParam("checkAnswer3")String checkAnswer3,
                           Model model) {
        /*发布错误的时候，用来保存原先输入的信息*/
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        model.addAttribute("name", name);
        model.addAttribute("email", email);

        /*校验*/
        if (username == null || username == "") {
            model.addAttribute("error", "用户名不能为空");
            return "publish";
        }
        if (password == null || password == "") {
            model.addAttribute("error", "密码不能为空");
            return "publish";
        }
        if (name == null || name == "") {
            model.addAttribute("error", "昵称不能为空");
            return "publish";
        }
        if (email == null || email == "") {
            model.addAttribute("error", "邮箱不能为空");
            return "publish";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);
        user.setGmtCreate(System.currentTimeMillis());
        user.setState(1L);
        user.setAvatarUrl("/images/default-avatar.png");
        userService.createUser(user);

        User user1 = userService.findByUsername1(username);
        UserCheckQuestion userCheckQuestion1 = new UserCheckQuestion();
        userCheckQuestion1.setUserId(user1.getId());
        userCheckQuestion1.setQuestion(checkQuestion1);
        userCheckQuestion1.setAnswer(checkAnswer1);
        userCheckQuestionService.createUserCheckQuestion(userCheckQuestion1);

        UserCheckQuestion userCheckQuestion2 = new UserCheckQuestion();
        userCheckQuestion2.setUserId(user1.getId());
        userCheckQuestion2.setQuestion(checkQuestion2);
        userCheckQuestion2.setAnswer(checkAnswer2);
        userCheckQuestionService.createUserCheckQuestion(userCheckQuestion2);

        UserCheckQuestion userCheckQuestion3 = new UserCheckQuestion();
        userCheckQuestion3.setUserId(user1.getId());
        userCheckQuestion3.setQuestion(checkQuestion3);
        userCheckQuestion3.setAnswer(checkAnswer3);
        userCheckQuestionService.createUserCheckQuestion(userCheckQuestion3);
        return "login";
    }

    @GetMapping("/toInformation")
    public String toInformation() {
        return "information";
    }

    @PostMapping("/editInformation")
    public String editInformation(@RequestParam("id") long id,
                                       @RequestParam("name") String name,
                                       @RequestParam("email") String email,
                                       @RequestParam("myfiles") MultipartFile myfiles,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        User user = new User();
        if (myfiles.getSize() != 0){
            Map<String, Object> map = (Map<String, Object>) FileController.uploadApk(myfiles, request, response);//调用工具类，将文件上传。特别要注意的是  参数中的第一个参数。MultipartFile myfiles,    myfiles 的名称要和页面中 <input type="file" name="myfiles" /> 中的name名称保持一致，否则获取不到你上传的文件，文件上传成功后会返回一个MAP集合。
            user.setAvatarUrl(map.get("path").toString());
        }else {
            String ognAvatarUrl = userService.findAvatarUrlById(id);
            user.setAvatarUrl(ognAvatarUrl);
        }
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        userService.editUser(user);
        return "redirect:/";
    }

    @GetMapping("/toBlacklist")
    public String toBlacklist(@RequestParam(name = "page",defaultValue = "1")Integer page,
                              @RequestParam(name = "size",defaultValue = "5")Integer size,
                              Model model){
        PaginationDTO pagination = userService.findFrozenUsers(page,size);
        model.addAttribute("pagination",pagination);
        return "blacklist";
    }

    @GetMapping("/toEditPassword")
    public String toEditPassword(){
        return "editpassword";
    }

    @PostMapping("/editPassword")
    public String editPassword(@RequestParam("id")Long id,
                               @RequestParam("newPassword")String newPassword){
        User user = new User();
        user.setId(id);
        user.setPassword(newPassword);
        userService.editPassword(user);
        return "redirect:/";
    }

    @ResponseBody
    @RequestMapping(value = "/checkOgnPassword/{id}",method = RequestMethod.GET)
    public User checkOgnPassword(@PathVariable("id")Long id){
        User user = userService.findByUserId(id);
        return user;
    }

    @PostMapping("/toCheckUser")
    public String toCheckUser(@RequestParam("username")String username,Model model){
        User user = userService.findByUsername1(username);
        List<UserCheckQuestion> userCheckQuestionList = userCheckQuestionService.findCheckQuestionByUserId(user.getId());
        UserCheckQuestion userCheckQuestion1 = userCheckQuestionList.get(0);
        UserCheckQuestion userCheckQuestion2 = userCheckQuestionList.get(1);
        UserCheckQuestion userCheckQuestion3 = userCheckQuestionList.get(2);
        model.addAttribute("checkQuestion1", userCheckQuestion1);
        model.addAttribute("checkQuestion2", userCheckQuestion2);
        model.addAttribute("checkQuestion3", userCheckQuestion3);
        return "checkuser";
    }

    @PostMapping("/toResetPassword")
    public String toResetPassword(@RequestParam("checkQuestion1")Long checkQuestion1,
                                  @RequestParam("checkAnswer1")String checkAnswer1,
                                  @RequestParam("checkQuestion2")Long checkQuestion2,
                                  @RequestParam("checkAnswer2")String checkAnswer2,
                                  @RequestParam("checkQuestion3")Long checkQuestion3,
                                  @RequestParam("checkAnswer3")String checkAnswer3,
                                  Model model){
        UserCheckQuestion userCheckQuestion1 = userCheckQuestionService.findCheckQuestion(checkQuestion1,checkAnswer1);
        UserCheckQuestion userCheckQuestion2 = userCheckQuestionService.findCheckQuestion(checkQuestion2, checkAnswer2);
        UserCheckQuestion userCheckQuestion3 = userCheckQuestionService.findCheckQuestion(checkQuestion3, checkAnswer3);
        if (userCheckQuestion1 == null){
            model.addAttribute("error","验证问题一的答案有误，请重新输入！");
            return "checkuser";
        }
        if (userCheckQuestion2 == null){
            model.addAttribute("error","验证问题二的答案有误，请重新输入！");
            return "checkuser";
        }
        if (userCheckQuestion3 == null){
            model.addAttribute("error","验证问题三的答案有误，请重新输入！");
            return "checkuser";
        }
        Long userId = userCheckQuestion1.getUserId();
        model.addAttribute("id",userId);
        return "resetpassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("id")Long id,
                                @RequestParam("newPassword")String newPassword){
        User user = new User();
        user.setId(id);
        user.setPassword(newPassword);
        userService.editPassword(user);
        return "login";
    }

    /*@ResponseBody
    @RequestMapping(value = "/checkLogin",method = RequestMethod.POST)
    public User checkLogin(@RequestBody String username,
                           @RequestBody String password){
        User user = userService.findByUsername(username,password);
        return user;
    }*/

    @ResponseBody
    @RequestMapping(value = "/checkUsername/{username}",method = RequestMethod.GET)
    public boolean checkUsername(@PathVariable("username")String username){
        User user = userService.findByUsername1(username);
        if (user == null){
            return true;
        }else {
            return false;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/checkName/{name}",method = RequestMethod.GET)
    public boolean checkName(@PathVariable("name")String name){
        User user = userService.checkName(name);
        if (user == null){
            return true;
        }else {
            return false;
        }
    }
}
