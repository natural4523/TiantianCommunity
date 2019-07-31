package cn.edu.sdju.soft.community.service;

import cn.edu.sdju.soft.community.dto.QuestionDTO;
import cn.edu.sdju.soft.community.mapper.QuestionMapper;
import cn.edu.sdju.soft.community.mapper.UserMapper;
import cn.edu.sdju.soft.community.model.Question;
import cn.edu.sdju.soft.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public List<QuestionDTO> list() {
        List<Question> questions = questionMapper.list();
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question:questions) {
            //根据Question对象里的creator查询到对应的user
            User user = userMapper.findById(question.getCreator());

            //将Qusetion对象里的属性封装到QuestionDTO对象里去
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//把question对象上的属性copy到questionDTO

            //将user对象封装到QuestionDTO里去
            questionDTO.setUser(user);

            //因为返回的对象是集合，所以把封装完毕的QuestionDTO对象装到集合里去返回
            questionDTOList.add(questionDTO);
        }
        return questionDTOList;
    }
}
