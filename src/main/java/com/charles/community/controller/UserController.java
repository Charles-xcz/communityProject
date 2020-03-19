package com.charles.community.controller;

import com.charles.community.annotation.LoginRequired;
import com.charles.community.model.User;
import com.charles.community.service.UserService;
import com.charles.community.util.CommunityUtil;
import com.charles.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author charles
 * @date 2020/3/19 10:34
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "site/setting";
    }

    /**
     * 更新头像
     *
     * @param headerImage 头像文件
     * @param model       model
     * @return 更新头像成功则重定向index页面
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "没有选择图片");
            return "site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        assert filename != null;
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "site/setting";
        }
        //生成文件随即名
        filename = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + filename);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败:{}", e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        //更新当前用户的头像路径,外部通过该路径访问头像
        User user = hostHolder.getUser();
        String headerUl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUl);
        return "redirect:/index";
    }

    /**
     * 获取头像
     *
     * @param filename 头像名
     * @param response 用于返回头像文件
     */
    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        //头像在服务器中存放的路径
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        FileInputStream fis = null;
        try {
            OutputStream os = response.getOutputStream();
            fis = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int i = 0;
            while ((i = fis.read(buffer)) != -1) {
                os.write(buffer, 0, i);
            }
        } catch (IOException e) {
            log.error("读取文件失败:{}", e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error("文件输入流关闭失败:{}", e.getMessage());
            }
        }
    }
}
