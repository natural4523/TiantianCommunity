package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.dto.PaginationDTO;
import cn.edu.sdju.soft.community.dto.QuestionDTO;
import cn.edu.sdju.soft.community.mapper.UserMapper;
import cn.edu.sdju.soft.community.model.Question;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1")Integer page,
                        @RequestParam(name = "size",defaultValue = "5")Integer size,
                        @RequestParam(name = "search",required = false)String search
                        ){
        /*service层需要抛出异常，只用controller的话可能同一种异常多处try-catch*/
        PaginationDTO pagination = questionService.list(search,page,size);
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);
        return "index";
    }

}
