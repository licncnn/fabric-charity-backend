package org.ncu.BlockChainCharity.service.serviceImpl;

import lombok.AllArgsConstructor;
import org.ncu.BlockChainCharity.bean.Charity;
import org.ncu.BlockChainCharity.config.HyperLedgerFabricProperties;
import org.ncu.BlockChainCharity.dto.*;
import org.ncu.BlockChainCharity.mapper.CharityMapper;
import org.ncu.BlockChainCharity.service.CharityService;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@AllArgsConstructor
public class CharityServiceImpl implements CharityService {

    static final String certifyPATH="/Users/dubai/fabric/hyperledger-fabric-app-java-demo/src/main/resources/static/upload/certify";
    static final String logoPATH="/Users/dubai/fabric/hyperledger-fabric-app-java-demo/src/main/resources/static/upload/logo";


    @Autowired
    CharityMapper charityMapper;

    @Autowired
    Logger logger;

    @Autowired
    final Gateway gateway;

    @Autowired
    final Contract contract;

    @Autowired
    final HyperLedgerFabricProperties hyperLedgerFabricProperties;




    /**
     * 机构注册，成功跳转login
     * @param charityName
     * @param password
     * @param confirmed_password
     * @param email
     * @param phoneNumber
     * @return
     */
    @Override
    public RegisterDTO charityRegister(String charityName, String password, String confirmed_password, String email, String phoneNumber) {
        RegisterDTO registerDTO = new RegisterDTO();
        Charity charity = charityMapper.getCharityByCharityName(charityName);
        Boolean passwordIs = password.equals(confirmed_password);
        if (charity != null){
            registerDTO.setError("机构已存在");
            registerDTO.setPath("register");
        }
        else if( !passwordIs ){
            registerDTO.setError("密码不一致");
            registerDTO.setPath("register");
        }
        else{
            charity = new Charity();
            charity.setCharityName(charityName);
            charity.setPassword(password);
            charity.setEmail(email);
            charity.setPhoneNumber(phoneNumber);
            //插入机构
            charityMapper.insertCharity(charity);



            String charityKey = String.format("CHARITY%03d",charity.getCharityId());
            charity.setCharityKey(charityKey);
            logger.info("成功插入一条charity注册数据,charityKey==>"+charityKey);
            // 更新KeycharityKey
            if(charityMapper.updateCharityKey(charity.getCharityId(), charityKey)==0){
                logger.error("更新项目键值失败");
                registerDTO.setError("更新charityKey出错");
            }

            registerDTO.setPath("login");
            registerDTO.setCharity(charity);
        }
        return registerDTO;
    }



    /**
     * 机构登录，成功跳转main
     * @param charityName
     * @param password
     * @return
     */
    @Override
    public LoginDTO charityLogin(String charityName, String password) {
        LoginDTO loginDTO = new LoginDTO();
        Charity charity = charityMapper.getCharityByCharityName(charityName);
        if (charity == null){
            loginDTO.setError("机构不存在");
        }
        else if ( ! charity.getPassword().equals(password)){
            loginDTO.setError("密码错误");
        }
        else{
            loginDTO.setCharity(charity);
        }
        return loginDTO;
    }


    @Override
    public CertifyDTO certify(Integer charityId, String idNumber, String address, String introduction, String certification, String logo) {
        CertifyDTO certifyDTO = new CertifyDTO();

//        Charity charity = charityMapper.getCharityByCharityId(charityId);

        Integer updateCharity = charityMapper.certify(charityId,idNumber,address,introduction,certification,logo);
        if (updateCharity==1){
            certifyDTO.setMessage("认证成功，等待审核");
        }
        else{
            certifyDTO.setMessage("认证文件上传失败");
        }
        return certifyDTO;
    }

    //TODO 未实现从区块链网络获得余额的方法
    //获取机构余额
    @Override
    public BlockchainDTO getCharityBalance(String charityKey) throws IOException {

        BlockchainDTO blockchainDTO = new BlockchainDTO();
//        try{
//            byte [] result = contract.submitTransaction("updateCat","asd");
//        } catch (EndorseException endorseException) {
//            endorseException.printStackTrace();
//            blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
//            return blockchainDTO;
//        } catch (SubmitException submitException) {
//            submitException.printStackTrace();
//            blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
//            return blockchainDTO;
//        } catch (CommitStatusException commitStatusException) {
//            commitStatusException.printStackTrace();
//            blockchainDTO.setError(ContractErrorMessage.CommitStatusException.getDis());
//            return blockchainDTO;
//        } catch (CommitException commitException) {
//            commitException.printStackTrace();
//            blockchainDTO.setError(ContractErrorMessage.CommitException.getDis());
//            return blockchainDTO;
//        }
        return blockchainDTO;
    }

    @Override
    public CharityDTO getCharityByKey(String charityKey) {
        final CharityDTO charityDTO = new CharityDTO();

//        System.out.println("------------charity name-------"+charityKey);

        Charity charity = charityMapper.getCharityByCharityKey(charityKey);
//        System.out.println(charity);


        charityDTO.setCharityName(charity.getCharityName());
        charityDTO.setCharityKey(charity.getCharityKey());
        charityDTO.setIsCertified(charity.getIsCertified());
        charityDTO.setIsAudited(charity.getIsAudited());

        charityDTO.setEmail(charity.getEmail());
        charityDTO.setAddress(charity.getAddress());
        charityDTO.setIntroduction(charity.getIntroduction());
        charityDTO.setCertificationPath(charity.getCertificationPath());
        charityDTO.setLogoPath(charity.getLogoPath());
        return charityDTO;
    }

    @Override
    public CharityDTO getCharityById(Integer charityId) {
        final CharityDTO charityDTO = new CharityDTO();
        final Charity charity = charityMapper.getCharityByCharityId(charityId);

        charityDTO.setCharityName(charity.getCharityName());
        charityDTO.setCharityKey(charity.getCharityKey());
        charityDTO.setIsCertified(charity.getIsCertified());
        charityDTO.setIsAudited(charity.getIsAudited());

        charityDTO.setEmail(charity.getEmail());
        charityDTO.setAddress(charity.getAddress());
        charityDTO.setIntroduction(charity.getIntroduction());
        charityDTO.setCertificationPath(charity.getCertificationPath());
        charityDTO.setLogoPath(charity.getLogoPath());



        return charityDTO;
    }

}
