package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.cache.TagCache;
import cn.edu.sdju.soft.community.dto.QuestionDTO;
import cn.edu.sdju.soft.community.mapper.QuestionMapper;
import cn.edu.sdju.soft.community.model.Question;
import cn.edu.sdju.soft.community.model.Section;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.service.QuestionService;
import cn.edu.sdju.soft.community.service.SectionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id")Long id,Model model){
        QuestionDTO question = questionService.getById(id);
        List<Section> sectionList = sectionService.findAllSections();
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());

        model.addAttribute("sectionId",question.getSectionId());
        model.addAttribute("sectionList",sectionList);
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        List<Section> sectionList = sectionService.findAllSections();
        model.addAttribute("sectionList",sectionList);
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(@RequestParam("title")String title,
                            @RequestParam("description")String description,
                            @RequestParam("tag")String tag,
                            @RequestParam("id")Long id,
                            @RequestParam("sectionId")Long sectionId,
                            HttpServletRequest request,
                            Model model){
        /*发布错误的时候，用来保存原先输入的信息*/
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());
        model.addAttribute("sectionId",sectionId);

        /*校验*/
        if (title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description == null || description == ""){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if (tag == null || tag == ""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        if (sectionId == null) {
            model.addAttribute("error","版块选择不能为空");
            return "publish";
        }

        String invalid = TagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(invalid)){
            model.addAttribute("error","输入非法标签:" + invalid);
            return "publish";
        }

        /*获取cookie中的user对象*/
        User user = (User) request.getSession().getAttribute("user");

        if (user == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        question.setSectionId(sectionId);
        questionService.createOrUpdate(question);

        return "redirect:/";
    }


}
