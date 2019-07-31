package cn.edu.sdju.soft.community.model;

import lombok.Data;

/**
 * Question对象本身是一个数据库模型，与数据库对应的，本身不需要User对象，抽出一个DTO，传输层的对象
 */
@Data
public class Question {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;//和User对象的id对应
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;


}
