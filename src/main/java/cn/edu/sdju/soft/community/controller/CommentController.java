package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.dto.CommentCreateDTO;
import cn.edu.sdju.soft.community.dto.CommentDTO;
import cn.edu.sdju.soft.community.dto.ResultDTO;
import cn.edu.sdju.soft.community.enums.CommentTypeEnum;
import cn.edu.sdju.soft.community.exception.CustomizeErrorCode;
import cn.edu.sdju.soft.community.model.Comment;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO
            , HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if(commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        comment.setCommentCount(0);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable("id")Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id,CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}
