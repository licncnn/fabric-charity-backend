package org.ncu.BlockChainCharity.dto;


import lombok.Data;
import org.ncu.BlockChainCharity.bean.Admin;
import org.ncu.BlockChainCharity.bean.Charity;
import org.ncu.BlockChainCharity.bean.User;

@Data
public class LoginDTO {
    User user;
    String error;
    Charity charity;
    String certifyMessage;
    Admin admin;
}
