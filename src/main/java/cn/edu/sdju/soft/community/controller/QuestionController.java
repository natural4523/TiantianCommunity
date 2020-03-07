package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.dto.CommentDTO;
import cn.edu.sdju.soft.community.dto.QuestionDTO;
import cn.edu.sdju.soft.community.enums.CommentTypeEnum;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.service.CommentService;
import cn.edu.sdju.soft.community.service.QuestionService;
import cn.edu.sdju.soft.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable("id")Long id, Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.incView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }

    @ResponseBody
    @RequestMapping(value = "/freeze/{id}",method = RequestMethod.GET)
    public User freeze(@PathVariable("id")long id){
        userService.freezeByUserId(id);
        User user  = userService.findByUserId(id);
        return user;
    }

    @ResponseBody
    @RequestMapping(value = "/unfreeze/{id}",method = RequestMethod.GET)
    public User unfreeze(@PathVariable("id")long id){
        userService.unfreezeByUserId(id);
        User user  = userService.findByUserId(id);
        return user;
    }

    @ResponseBody
    @RequestMapping(value = "/findById/{id}",method = RequestMethod.GET)
    public User findById(@PathVariable("id")long id){
        User user = userService.findByUserId(id);
        return user;
    }

    @GetMapping("/deleteMyQuestion/{id}")
    public String deleteMyQuestion(@PathVariable("id")Long id){
        questionService.deleteMyQuestion(id);
        return "redirect:/profile/questions";
    }

    @GetMapping("/deleteOtherQuestion/{id}")
    public String deleteOtherQuestion(@PathVariable("id")Long id){
        questionService.deleteMyQuestion(id);
        return "redirect:/";
    }
}
