package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.model.Section;
import cn.edu.sdju.soft.community.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @PostMapping("/addSection")
    public String addSection(@RequestParam("name")String name,
                             @RequestParam("id")Long id){
        Section section = new Section();
        section.setCreator(id);
        section.setName(name);
        section.setGmtCreate(System.currentTimeMillis());
        sectionService.addSection(section);
        return "redirect:/";
    }

    @PostMapping("/editSection")
    public String editSection(@RequestParam("sectionId")Long sectionId,
                              @RequestParam("name")String name){
        Section section = new Section();
        section.setGmtModified(System.currentTimeMillis());
        section.setId(sectionId);
        section.setName(name);
        sectionService.editSection(section);
        return "redirect:/";
    }

    @PostMapping("/deleteSection")
    public String deleteSection(@RequestParam("sectionId")Long sectionId){
        sectionService.deleteSection(sectionId);
        return "redirect:/";
    }

}
