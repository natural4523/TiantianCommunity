package cn.edu.sdju.soft.community.cache;

import cn.edu.sdju.soft.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {
    public static List<TagDTO> get() {
        List<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategoryName("生活");
        program.setTags(Arrays.asList("校园趣事", "影视娱乐", "游戏", "交友", "晒照片", "体育", "搞笑趣味","音乐分享","其他"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("学习");
        framework.setTags(Arrays.asList("语文", "英语", "数学", "物理", "化学", "生物", "计算机","其他"));
        tagDTOS.add(framework);


        TagDTO server = new TagDTO();
        server.setCategoryName("求助");
        server.setTags(Arrays.asList("寻人", "寻物", "题目求解", "新生求助", "其他"));
        tagDTOS.add(server);

        return tagDTOS;
    }

    public static String filterInvalid(String tags) {
        String[] split = StringUtils.split(tags, "，");
        List<TagDTO> tagDTOS = get();
        //拿到所有的标签,0 javascript 1 php 2 css 3 html ... 75 hg
        //flatMap拿到第二层集合里的元素
        List<String> tagList = tagDTOS.stream().flatMap(tagDTO -> tagDTO.getTags().stream()).collect(Collectors.toList());
        //获得出错的标签,多个的话用","分隔
        String invalid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
