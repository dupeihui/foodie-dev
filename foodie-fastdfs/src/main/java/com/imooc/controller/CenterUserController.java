package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.resource.FileUpload;
import com.imooc.service.FastdfsService;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("fdfs")
public class CenterUserController extends BaseController {


    @Autowired
    private FastdfsService fastdfsService;

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    private CenterUserService centerUserService;

    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(
            String userId,
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        String path = null;

        if(file != null){
            // 获得文件上传的文件名称
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {

                // 文件重命名  imooc-face.png -> ["imooc-face", "png"]
                String fileNameArr[] = fileName.split("\\.");

                // 获取文件的后缀名
                String suffix = fileNameArr[fileNameArr.length - 1];

                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")) {
                    return IMOOCJSONResult.errorMsg("图片格式不正确！");
                }

                path = fastdfsService.upload(file, suffix);
                System.out.println(path);
            }

            if(StringUtils.isNotBlank(path)){
                String finalUserFaceUrl = fileUpload.getHost() + path;

                // 更新用户头像到数据库
                Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);

                UsersVO usersVO = conventUsersVO(userResult);

                CookieUtils.setCookie(request, response, "user",
                        JsonUtils.objectToJson(usersVO), true);
            } else {
                return IMOOCJSONResult.errorMsg("文件上传失败！");
            }
        } else {
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }

        return IMOOCJSONResult.ok();

    }


}
