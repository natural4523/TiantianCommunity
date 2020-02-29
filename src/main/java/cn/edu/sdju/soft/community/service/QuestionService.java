package cn.edu.sdju.soft.community.service;

import cn.edu.sdju.soft.community.dto.PaginationDTO;
import cn.edu.sdju.soft.community.dto.QuestionDTO;
import cn.edu.sdju.soft.community.dto.QuestionQueryDTO;
import cn.edu.sdju.soft.community.exception.CustomizeErrorCode;
import cn.edu.sdju.soft.community.exception.CustomizeException;
import cn.edu.sdju.soft.community.mapper.QuestionExtMapper;
import cn.edu.sdju.soft.community.mapper.QuestionMapper;
import cn.edu.sdju.soft.community.mapper.UserExtMapper;
import cn.edu.sdju.soft.community.mapper.UserMapper;
import cn.edu.sdju.soft.community.model.Question;
import cn.edu.sdju.soft.community.model.QuestionExample;
import cn.edu.sdju.soft.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserExtMapper userExtMapper;

    public PaginationDTO list(String search,Long sectionId,Integer page, Integer size) {
        if (StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }


        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        //Integer totalCount = questionMapper.count();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setSectionId(sectionId);
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);

        if (totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1){
            page = 1;
        }

        if (page > totalPage){
            page = totalPage;
        }

        if(page == 0){
            page = 1;
        }
        paginationDTO.setPagination(totalPage,page);


        //size*(page-1)=偏移量
        Integer offset = size * (page - 1);
//        List<Question> questions = questionMapper.list(offset,size);
        //QuestionExample questionExample = new QuestionExample();
        //questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);

//        List<Question> questions = questionExtMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question:questions) {
            //根据Question对象里的creator查询到对应的user
            User user = userMapper.selectByPrimaryKey(question.getCreator());

            //将Qusetion对象里的属性封装到QuestionDTO对象里去
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//把question对象上的属性copy到questionDTO
            questionDTO.setId(question.getId());
            //将user对象封装到QuestionDTO里去
            questionDTO.setUser(user);

            //因为返回的对象是集合，所以把封装完毕的QuestionDTO对象装到集合里去返回
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
//        Integer totalCount = questionMapper.countByUserId(userId);

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);

        if (totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1){
            page = 1;
        }

        if (page > totalPage && totalPage != 0){
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage,page);
        //size*(page-1)=偏移量
        Integer offset = size * (page - 1);
//        List<Question> questions = questionMapper.listByUserId(userId,offset,size);

        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question:questions) {
            //根据Question对象里的creator查询到对应的user
            User user = userMapper.selectByPrimaryKey(question.getCreator());

            //将Qusetion对象里的属性封装到QuestionDTO对象里去
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//把question对象上的属性copy到questionDTO

            //将user对象封装到QuestionDTO里去
            questionDTO.setUser(user);

            //因为返回的对象是集合，所以把封装完毕的QuestionDTO对象装到集合里去返回
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        //根据Question对象里的creator查询到对应的user
        //User user = userMapper.selectByPrimaryKey(question.getCreator());
        User user = userExtMapper.findByUserId(question.getCreator());
        //将user对象封装到QuestionDTO里去
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null){
            //创建问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
//            questionMapper.create(question);
            questionMapper.insert(question);
        }else {
            //更新
            question.setGmtModified(question.getGmtCreate());
//            questionMapper.update(question);
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setSectionId(question.getSectionId());

            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());

            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {
       /* Question question = questionMapper.selectByPrimaryKey(id);
        Question updateQuestion = new Question();
        updateQuestion.setViewCount(question.getViewCount() + 1);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andIdEqualTo(id);*/

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
        //questionMapper.updateByExampleSelective(updateQuestion, questionExample);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), "，");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
