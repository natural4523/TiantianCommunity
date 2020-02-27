package cn.edu.sdju.soft.community.controller;

import cn.edu.sdju.soft.community.dto.FileDTO;
import cn.edu.sdju.soft.community.mapper.UserExtMapper;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.provider.UCloudProvider;
import cn.edu.sdju.soft.community.service.UserService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileController {

    @Autowired
    private UCloudProvider uCloudProvider;

    @Autowired
    private UserService userService;

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        try {
            String fileName = uCloudProvider.upload(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl(fileName);
            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/js.jpg");
        return fileDTO;
    }


    /*@PostMapping("/editInformation")
    public String editInformation(@RequestParam("id")long id,
                                  @RequestParam("name")String name,
                                  @RequestParam("email")String email,
                                  @RequestParam("file")MultipartFile file,
                                  Model model,
                                  HttpServletRequest request) throws IOException {
        User user = new User();
        //获取项目classes/static的地址
        String staticPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
        String fileName = file.getOriginalFilename();  //获取文件名

        // 图片存储目录及图片名称
        String url_path = "images" + File.separator + fileName;
        //图片保存路径
        String savePath = staticPath + File.separator + url_path;
        System.out.println("图片保存地址："+savePath);
        // 访问路径=静态资源路径+文件目录路径
        String visitPath ="static/" + url_path;
        System.out.println("图片访问uri："+visitPath);

        File saveFile = new File(savePath);
        if (!saveFile.exists()){
            saveFile.mkdirs();
        }
        try {
            file.transferTo(saveFile);  //将临时存储的文件移动到真实存储路径下
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setAvatarUrl(visitPath);
        userService.editUser(user);
        return "redirect:/";

        if (!file.isEmpty()){
            String realPath = "\\Path\\";
            System.out.println("path=" + realPath);
            String filename = file.getOriginalFilename();
            File filepath =new File(realPath,filename);
            user.setId(id);
            user.setName(name);
            user.setEmail(email);
            user.setAvatarUrl(filepath.toString());
            userService.editUser(user);
            return "redirect:/";
        }else {
            model.addAttribute("error","修改资料失败！");
            return "redirect:/information";
        }
    }*/

    //图片上传
    @RequestMapping("/upload")
    public static Object uploadApk(MultipartFile myfiles,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        if (myfiles.getSize() > 1024 * 1024 * 5) {
            resMap.put("code", 500);
            resMap.put("msg", "文件过大，请上传5M以内的图片");
            System.out.println("文件上传失败");
            return resMap;
        }
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
        Date dt = new Date();
        Long time = dt.getTime();
        if (myfiles != null) {
            String realPath = "d://uploadFiles/"; // 获取保存的路径，本地磁盘中的一个文件
            if (myfiles.isEmpty()) {
                // 未选择文件
                // resMap.put("code", 400);
                // resMap.put("msg", "未选择文件");
            } else {
                // 文件原名称
                String originFileName = "";
                // 上传文件重命名
                String originalFilename = time.toString().substring(time.toString().length() - 8,time.toString().length());
                originalFilename = originalFilename.concat(".");
                originalFilename = originalFilename.concat(myfiles.getOriginalFilename().toString()
                        .substring(myfiles.getOriginalFilename().toString().indexOf(".") + 1));
                try {
                    // 这里使用Apache的FileUtils方法来进行保存
                    FileUtils.copyInputStreamToFile(myfiles.getInputStream(), new File(realPath, originalFilename));
                    resMap.put("code", 200);
                    resMap.put("msg", "上传成功");
                    resMap.put("filename", originalFilename);
                    resMap.put("path", basePath + "/static/images/" + originalFilename);

                } catch (IOException e) {
                    resMap.put("code", 500);
                    System.out.println("文件上传失败");
                    resMap.put("msg", "文件上传失败");
                    e.printStackTrace();
                }
            }

        }

        String param = JSON.toJSONString(resMap);
        System.out.println(param);
        return resMap;
    }



}
