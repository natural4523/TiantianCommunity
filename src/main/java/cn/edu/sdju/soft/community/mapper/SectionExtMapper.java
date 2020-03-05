package cn.edu.sdju.soft.community.mapper;

import cn.edu.sdju.soft.community.model.Section;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SectionExtMapper {
    List<Section> findAllSections();

    void addSection(Section section);

    void editSection(Section section);

    void deleteSection(@Param("sectionId") Long sectionId);
}
