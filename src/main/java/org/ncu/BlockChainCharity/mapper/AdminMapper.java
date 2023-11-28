package org.ncu.BlockChainCharity.mapper;


import org.ncu.BlockChainCharity.bean.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    Admin getAdminByName(String adminName);
    //插入管理员
    void insertAdmin(Admin admin);
}
