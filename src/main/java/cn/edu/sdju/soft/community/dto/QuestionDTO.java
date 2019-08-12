package cn.edu.sdju.soft.community.dto;

import cn.edu.sdju.soft.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;//和User对象的id对应
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
