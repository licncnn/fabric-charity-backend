package org.ncu.BlockChainCharity.service;


import org.ncu.BlockChainCharity.dto.*;


import java.io.IOException;


public interface CharityService  {

    RegisterDTO charityRegister(String charityName, String password, String confirmed_password, String email, String phoneNumber);
    LoginDTO charityLogin(String charityName, String password);
    CertifyDTO certify(Integer charityId, String idNumber, String address, String introduction, String certification, String logo);
    public BlockchainDTO getCharityBalance(String charityKey) throws IOException;

    CharityDTO getCharityByKey(String charityKey);

    CharityDTO getCharityById(Integer charityId);
}