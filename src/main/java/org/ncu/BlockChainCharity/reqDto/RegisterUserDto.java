package org.ncu.BlockChainCharity.reqDto;

import lombok.Data;

@Data
public class RegisterUserDto {
    String username;
    String password;
    String passwordConfirmed;
    String email;
    String phoneNumber;
    Integer age;
    Integer sex;
}
