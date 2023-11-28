package org.ncu.BlockChainCharity.service;


import org.ncu.BlockChainCharity.bean.Charity;
import org.ncu.BlockChainCharity.dto.LoginDTO;
import org.ncu.BlockChainCharity.dto.RegisterDTO;

import java.util.List;

public interface AdminService {
    RegisterDTO adminRegister(String adminName, String password, String passwordConfirmed, String email, String phoneNumber);
    LoginDTO adminLogin(String realName, String password);
    Integer auditCharity(Integer charityId);
    //查询待审核机构 （认证但未审核机构）
    List<Charity> getUnderAuditedCharity();
}
