package org.ncu.BlockChainCharity.dto;


import lombok.Data;
import org.ncu.BlockChainCharity.bean.Admin;
import org.ncu.BlockChainCharity.bean.Charity;
import org.ncu.BlockChainCharity.bean.User;

@Data
public class RegisterDTO
{
    User user;
    String path;
    String error;
    Charity charity;
    Admin admin;
}
