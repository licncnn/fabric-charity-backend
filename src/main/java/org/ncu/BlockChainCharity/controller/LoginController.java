package org.ncu.BlockChainCharity.controller;

import com.alibaba.fastjson.JSON;
import org.ncu.BlockChainCharity.bean.Admin;
import org.ncu.BlockChainCharity.bean.Charity;
import org.ncu.BlockChainCharity.bean.User;
import org.ncu.BlockChainCharity.dto.LoginDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.reqDto.UserDto;
import org.ncu.BlockChainCharity.service.AdminService;
import org.ncu.BlockChainCharity.service.CharityService;
import org.ncu.BlockChainCharity.service.UserService;
import org.ncu.BlockChainCharity.utils.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("user")
public class LoginController {

    @Autowired
    UserService userService;
    @Autowired
    CharityService charityService;
    @Autowired
    AdminService adminService;


    // 用户登录
    @ResponseBody
    @GetMapping("getInfo")
    public ResponseEntity getInfo(HttpSession session){

        Userinfo userinfo = new Userinfo();

        String role = (String)session.getAttribute("role");
//        System.out.println("role=====>"+role);
        if(role=="admin"){
            userinfo.setRoles(new String[]{"admin"});
            final Admin admin = (Admin) session.getAttribute("admin");
            userinfo.setName(admin.getRealName());
            userinfo.setId(Long.valueOf(admin.getAdminId()));
            userinfo.setAvatar("http://localhost:8083/platform/image/getUploadPicture?path=avatar/manager.jpg");
            userinfo.setIntroduction("phone:"+admin.getPhoneNumber()+",eamil:"+admin.getEmail());
            userinfo.setKey("");
            userinfo.setType("admin");
        }else if(role=="charity"){
            userinfo.setRoles(new String[]{"charity"});
            final Charity charity = (Charity) session.getAttribute("charity");

            userinfo.setName(charity.getCharityName());
            userinfo.setId(Long.valueOf(charity.getCharityId()));
//            if(charity.getLogoPath() != null && charity.getLogoPath()!="" ){
//                userinfo.setAvatar(charity.getLogoPath());
//            }else{
                userinfo.setAvatar("http://localhost:8083/platform/image/getUploadPicture?path=avatar/charity.jpg");
//            }
            userinfo.setIntroduction("phone:"+charity.getPhoneNumber()+",eamil:"+charity.getEmail());
            userinfo.setKey(charity.getCharityKey());
            userinfo.setType("charity");

        }else {
            // user
            userinfo.setRoles(new String[]{"user"});
            final User user = (User)session.getAttribute("user");
            System.out.println(user);

            userinfo.setName(user.getUserName());
            userinfo.setId(Long.valueOf(user.getUserId()));
            userinfo.setAvatar("http://localhost:8083/platform/image/getUploadPicture?path=avatar/user.gif");
            userinfo.setIntroduction("phone:"+user.getPhoneNumber()+",eamil:"+user.getEmail());
            userinfo.setKey("");
            userinfo.setType("user");

        }


        return new ResponseEntity(200,"getInfo成功", JSON.toJSON(userinfo));
    }



    // 用户登录
    @ResponseBody
    @PostMapping("userLogin")
    public ResponseEntity loginUser(@RequestBody UserDto userdto, HttpSession session){
        System.out.println(userdto.getUsername());
        System.out.println(userdto.getPassword());

        LoginDTO loginDTO =  userService.login(userdto.getUsername(),userdto.getPassword());
        String error = loginDTO.getError();
        if(error!=null) {
            return new ResponseEntity(400,error,null);
        }else {
            session.setAttribute("user",loginDTO.getUser());
            session.setAttribute("role","user");

            return new ResponseEntity(200,"登录成功",session.getId());
        }
    }

    // 机构登录
    @ResponseBody
    @PostMapping("charityLogin")
    public ResponseEntity loginCharity(@RequestBody UserDto userdto, HttpSession session){

        LoginDTO loginDTO =  charityService.charityLogin(userdto.getUsername(), userdto.getPassword());
        String error = loginDTO.getError();
        if (error != null){
            return new ResponseEntity(400,error,null);
        }else {
            session.setAttribute("charity",loginDTO.getCharity());
            session.setAttribute("role","charity");
            return new ResponseEntity(200,"登录成功",session.getId());
        }
    }

    //管理员登录
    @ResponseBody
    @PostMapping("adminLogin")
    public ResponseEntity loginAdmin(@RequestBody UserDto userdto, HttpSession session){
        LoginDTO loginDTO = adminService.adminLogin(userdto.getUsername(),userdto.getPassword());
        String error = loginDTO.getError();
        if (error!=null){
            return new ResponseEntity(400,error,null);
        }
        else{
            session.setAttribute("admin",loginDTO.getAdmin());
            session.setAttribute("role","admin");
            return new ResponseEntity(200,"登录成功",session.getId());
        }
    }

    //退出
    @ResponseBody
    @GetMapping("logout")
    public ResponseEntity logout(HttpSession session){
        session.invalidate();
        return new ResponseEntity(200,"success",null);
    }

}
