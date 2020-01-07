package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.dto.PaginationDTO;
import cn.edu.sdju.soft.community.dto.QuestionDTO;
import cn.edu.sdju.soft.community.mapper.UserMapper;
import cn.edu.sdju.soft.community.model.Question;
import cn.edu.sdju.soft.community.model.Section;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.service.QuestionService;
import cn.edu.sdju.soft.community.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1")Integer page,
                        @RequestParam(name = "size",defaultValue = "5")Integer size,
                        @RequestParam(name = "search",required = false)String search,
                        @RequestParam(name = "sectionId", required = false) Long sectionId
                        ){
        /*service层需要抛出异常，只用controller的话可能同一种异常多处try-catch*/
        PaginationDTO pagination = questionService.list(search,sectionId,page,size);
        //总页数不对
        List<Section> sectionList = sectionService.findAllSections();
        model.addAttribute("sectionList",sectionList);
        model.addAttribute("sectionId",sectionId);
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);
        return "index";
    }


}
