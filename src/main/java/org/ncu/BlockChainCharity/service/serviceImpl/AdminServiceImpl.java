package org.ncu.BlockChainCharity.service.serviceImpl;


import org.ncu.BlockChainCharity.bean.Admin;
import org.ncu.BlockChainCharity.bean.Charity;
import org.ncu.BlockChainCharity.dto.LoginDTO;
import org.ncu.BlockChainCharity.dto.RegisterDTO;
import org.ncu.BlockChainCharity.mapper.AdminMapper;
import org.ncu.BlockChainCharity.mapper.CharityMapper;
import org.ncu.BlockChainCharity.service.AdminService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    Logger logger;

    @Autowired
    AdminMapper adminMapper;
    @Autowired
    CharityMapper charityMapper;

    /**
     * 管理员注册，注册成功返回登录页
     * @param adminName
     * @param password
     * @param passwordConfirmed
     * @param email
     * @param phoneNumber
     * @return
     */
    @Override
    public RegisterDTO adminRegister(String adminName, String password, String passwordConfirmed, String email, String phoneNumber) {
        RegisterDTO registerDTO = new RegisterDTO();
        Admin admin = adminMapper.getAdminByName(adminName);
        if (admin!=null){
            registerDTO.setError("用户已存在");
            registerDTO.setPath("register");
        }
        else if (!password.equals(passwordConfirmed)){
            registerDTO.setError("密码不一致");
            registerDTO.setPath("register");
        }
        else {
            admin = new Admin();
            admin.setRealName(adminName);
            admin.setPassword(password);
            admin.setEmail(email);
            admin.setPhoneNumber(phoneNumber);
            Timestamp date = new Timestamp(new Date().getTime());
            admin.setCreateTime(date);
            //插入admin
            adminMapper.insertAdmin(admin);
            registerDTO.setAdmin(admin);
            registerDTO.setPath("login");
        }
        return registerDTO;
    }


    @Override
    public LoginDTO adminLogin(String realName, String password) {
        LoginDTO loginDTO = new LoginDTO();
        Admin admin = adminMapper.getAdminByName(realName);
        if (admin==null){
            loginDTO.setError("用户不存在");
        }
        else if (!password.equals(admin.getPassword())){
            loginDTO.setError("密码错误");
        }
        else {
            loginDTO.setAdmin(admin);
        }
        return loginDTO;
    }

    @Override
    public Integer auditCharity(Integer charityId) {
        charityMapper.audit(charityId,1);
        return 1;
    }

    @Override
    public List<Charity> getUnderAuditedCharity() {
        return charityMapper.getUnderAuditedCharity();
    }


}
