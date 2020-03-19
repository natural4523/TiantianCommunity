package cn.edu.sdju.soft.community.service;

import cn.edu.sdju.soft.community.mapper.SectionExtMapper;
import cn.edu.sdju.soft.community.model.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    @Autowired
    private SectionExtMapper sectionExtMapper;

    public  List<Section> findAllSections() {
        List<Section> sectionList = sectionExtMapper.findAllSections();
        return sectionList;
    }


    public void addSection(Section section) {
        sectionExtMapper.addSection(section);
    }

    public void editSection(Section section) {
        sectionExtMapper.editSection(section);
    }

    public void deleteSection(Long sectionId) {
        sectionExtMapper.deleteSection(sectionId);
    }

    public Section checkSection(String name) {
        Section section = sectionExtMapper.checkSection(name);
        return section;
    }

    public Section checkEditSectionById(Long id) {
        Section section = sectionExtMapper.checkEditSectionById(id);
        return section;
    }
}
