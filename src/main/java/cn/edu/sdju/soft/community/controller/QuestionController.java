package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.dto.CommentDTO;
import cn.edu.sdju.soft.community.dto.QuestionDTO;
import cn.edu.sdju.soft.community.enums.CommentTypeEnum;
import cn.edu.sdju.soft.community.service.CommentService;
import cn.edu.sdju.soft.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable("id")Long id, Model model){
        QuestionDTO questionDTO = questionService.getById(id);

        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.incView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        return "question";
    }
}
