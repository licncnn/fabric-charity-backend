package org.ncu.BlockChainCharity.controller;


import org.ncu.BlockChainCharity.dto.RegisterDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.reqDto.RegisterUserDto;
import org.ncu.BlockChainCharity.service.AdminService;
import org.ncu.BlockChainCharity.service.CharityService;
import org.ncu.BlockChainCharity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    UserService userService;
    @Autowired
    CharityService charityService;
    @Autowired
    AdminService adminService;


    // 用户注册
    @PostMapping("user")
    public ResponseEntity userRegister(@RequestBody RegisterUserDto registerUserDto, HttpSession session){
        System.out.println(registerUserDto.getUsername());
        System.out.println(registerUserDto.getPassword());
        System.out.println("---------------------------------------------");

        RegisterDTO registerDTO = userService.userRegister(registerUserDto.getUsername(), registerUserDto.getPassword(), registerUserDto.getPassword(), registerUserDto.getEmail(), registerUserDto.getPhoneNumber(), registerUserDto.getAge(), registerUserDto.getSex());

        String error = registerDTO.getError();
        if(error != null ){
              return new ResponseEntity(400,error,null);
        }else {
            session.setAttribute("user",registerDTO.getUser());
            return new ResponseEntity(200,"注册成功",null);
        }
    }

    // 机构注册
    @PostMapping("charity")
    public ResponseEntity charityRegister(@RequestBody RegisterUserDto registerUserDto, HttpSession session){
        RegisterDTO registerDTO = charityService.charityRegister(registerUserDto.getUsername(), registerUserDto.getPassword(), registerUserDto.getPassword(), registerUserDto.getEmail(), registerUserDto.getPhoneNumber());
        String error = registerDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        session.setAttribute("charity",registerDTO.getCharity());
        return new ResponseEntity(200,"注册成功",null);
    }

    //管理员注册
    @PostMapping("admin")
    public ResponseEntity adminRegister(@RequestBody RegisterUserDto registerUserDto, HttpSession session){
        RegisterDTO registerDTO =adminService.adminRegister(registerUserDto.getUsername(), registerUserDto.getPassword(), registerUserDto.getPassword(), registerUserDto.getEmail(), registerUserDto.getPhoneNumber());
        String error = registerDTO.getError();
        if (error!=null){
            return new ResponseEntity(400,error,null);
        }
        session.setAttribute("admin",registerDTO.getAdmin());
        return new ResponseEntity(200,"注册成功",null);
    }

}
