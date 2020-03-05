package cn.edu.sdju.soft.community.model;

import lombok.Data;


@Data
public class UserCheckQuestion {
    private Long id;
    private Long question;
    private String answer;
    private Long userId;
    private CheckQuestions checkQuestions;
}
