package org.ncu.BlockChainCharity.service;

import org.ncu.BlockChainCharity.dto.LoginDTO;
import org.ncu.BlockChainCharity.dto.RegisterDTO;

public interface UserService  {
    RegisterDTO userRegister(String username, String password, String passwordConfirmed, String email, String phoneNumber, Integer age, Integer sex);
    LoginDTO login(String username, String password);
}
