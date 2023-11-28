package org.ncu.BlockChainCharity.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import org.hyperledger.fabric.client.*;
import org.ncu.BlockChainCharity.bean.WithdrawRecord;
import org.ncu.BlockChainCharity.common.ContractErrorMessage;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.DrawRecordRto;
import org.ncu.BlockChainCharity.dto.WithdrawDTO;
import org.ncu.BlockChainCharity.mapper.ContributionMapper;
import org.ncu.BlockChainCharity.mapper.WithdrawMapper;
import org.ncu.BlockChainCharity.service.WithDrawService;
import org.ncu.BlockChainCharity.utils.HybridEncryptionUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class WithDrawServiceImpl implements WithDrawService {

    @Autowired
    final Gateway gateway;

    @Autowired
    final Contract contract;

    @Autowired
    WithdrawMapper withdrawMapper;


    @Override
    public BlockchainDTO getAllWithDrawRecords() throws Exception {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        byte[] result;
        try{
            result = contract.evaluateTransaction("getAllWithDrawRecord");
//            System.out.println(new String(result));

            String resultStr = new String(result, StandardCharsets.UTF_8);
//            System.out.println(resultStr);

            List<DrawRecordRto> withdrawRecords = JSON.parseObject(resultStr, new TypeReference<List<DrawRecordRto>>() {});


            // Now you can use the withdrawRecords list
            for (DrawRecordRto record : withdrawRecords) {
                if (record.getSignature()==null || record.getSignature()==""){
                    continue;
                }
                byte[] bytes_signature = Base64.getDecoder().decode(record.getSignature());
                String all_Secret_Msg = record.getBank_name()+record.getBank_id_card()+record.getName()+record.getId_card();

                boolean signatureIsValid = HybridEncryptionUtil.verifySignature(HybridEncryptionUtil.eccPublicKey, all_Secret_Msg.getBytes(), bytes_signature);
                if (!signatureIsValid){
                    System.out.println("签名验证失败");
                }else{
                    System.out.println("签名验证成功");
                    record.setBank_name(HybridEncryptionUtil.decryptAES(HybridEncryptionUtil.aesKey,Base64.getDecoder().decode(record.getBank_name())));
                    record.setBank_id_card(HybridEncryptionUtil.decryptAES(HybridEncryptionUtil.aesKey,Base64.getDecoder().decode(record.getBank_id_card())));
                    record.setName(HybridEncryptionUtil.decryptAES(HybridEncryptionUtil.aesKey,Base64.getDecoder().decode(record.getName())));
                    record.setId_card(HybridEncryptionUtil.decryptAES(HybridEncryptionUtil.aesKey,Base64.getDecoder().decode(record.getId_card())));
                    System.out.println("解密后数据："+record);
                }
            }


            blockchainDTO.setResult(JSON.toJSONString(withdrawRecords));

        } catch (EndorseException endorseException) {
            endorseException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
            return blockchainDTO;
        } catch (SubmitException submitException) {
            submitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
            return blockchainDTO;
        } catch (CommitStatusException commitStatusException) {
            commitStatusException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.CommitStatusException.getDis());
            return blockchainDTO;
        } catch (GatewayException contractException){
            blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
            contractException.printStackTrace();
        }
        return blockchainDTO;
    }

    @Override
    public BlockchainDTO addWithDrawRecord(WithdrawDTO withdrawDTO) throws Exception {
        BlockchainDTO blockchainDTO = new BlockchainDTO();

        final WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setAmount(withdrawDTO.getAmount());
        withdrawRecord.setProjectKey(withdrawDTO.getProjectKey());
        withdrawRecord.setCharityKey(withdrawDTO.getCharityKey());

        // 数据库里面插入未加密的数据
        withdrawRecord.setBank_name(withdrawDTO.getBank_name());
        withdrawRecord.setBank_id_card(withdrawDTO.getBank_id_card());
        withdrawRecord.setName(withdrawDTO.getName());
        withdrawRecord.setId_card(withdrawDTO.getId_card());

        String base64encry_Bank_name = Base64.getEncoder().encodeToString(HybridEncryptionUtil.encryptAES(HybridEncryptionUtil.aesKey, withdrawDTO.getBank_name()));
        String base64encry_Bank_id_card = Base64.getEncoder().encodeToString(HybridEncryptionUtil.encryptAES(HybridEncryptionUtil.aesKey, withdrawDTO.getBank_id_card()));
        String base64encry_Name = Base64.getEncoder().encodeToString(HybridEncryptionUtil.encryptAES(HybridEncryptionUtil.aesKey, withdrawDTO.getName()));
        String base64encry_Id_card = Base64.getEncoder().encodeToString(HybridEncryptionUtil.encryptAES(HybridEncryptionUtil.aesKey, withdrawDTO.getId_card()));

//        withdrawRecord.setBank_name(encry_Bank_name);
//        withdrawRecord.setBank_id_card(encry_Bank_id_card);
//        withdrawRecord.setName(encry_Name);
//        withdrawRecord.setId_card(encry_Id_card);


        String All_Secret_Msg = base64encry_Bank_name+base64encry_Bank_id_card+base64encry_Name+base64encry_Id_card;
        byte[] signature = HybridEncryptionUtil.sign(HybridEncryptionUtil.eccPrivateKey, All_Secret_Msg.getBytes());

        // 生成base64字符串签名
        String base64str_signature = Base64.getEncoder().encodeToString(signature);
        System.out.println(base64str_signature);

        // 生成base64字符串公钥
        String base64str_publicKeyString = HybridEncryptionUtil.publicKeyToString(HybridEncryptionUtil.eccPublicKey);
        System.out.println("Public key as string: " + base64str_publicKeyString);

        withdrawRecord.setComment(withdrawDTO.getComment());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        withdrawRecord.setDatetime(timestamp);


        // fix  withdrawKey
        if (withdrawMapper.insertWithDrawRecord(withdrawRecord)==0){
//            logger.error("上传提现数据失败");
            blockchainDTO.setError("上传提现数据失败");
            return blockchainDTO;
        }
        // fix
        final Integer withdrawId = withdrawRecord.getWithdrawId();
        String withdrawKey = String.format("WITHDRAW%03d",withdrawId);

        if(withdrawMapper.updateWithDrawRecord(withdrawId,withdrawKey)==0){
//            logger.error("更新提现记录键值失败");
            blockchainDTO.setError("更新提现记录失败");
            return blockchainDTO;
        }
        //  上传到区块链中


        byte[] result;
        try {
            final SubmittedTransaction createWithdrawRecord = contract.newProposal("createWithdrawRecord")
                    .addArguments(withdrawKey, withdrawRecord.getCharityKey(), withdrawRecord.getProjectKey(),
                            withdrawRecord.getAmount()+"", base64encry_Bank_name,base64encry_Bank_id_card,
                            base64encry_Name,base64encry_Id_card,
                            withdrawRecord.getComment(),withdrawRecord.getDatetime()+"",base64str_signature,base64str_publicKeyString)
                    .build()
                    .endorse()
                    .submitAsync();


//            System.out.println(createWithdrawRecord.getResult());
            System.out.println("交易id==>"+createWithdrawRecord.getTransactionId());
            withdrawRecord.setTransactionId(createWithdrawRecord.getTransactionId());

            if(withdrawMapper.updateWithdrawRecordTransaction(withdrawId,createWithdrawRecord.getTransactionId())==0){
//                logger.error("更新项目键值失败");
                blockchainDTO.setError("上传出错");
                return blockchainDTO;
            }
            //
            result = createWithdrawRecord.getResult();
            blockchainDTO.setTransactionId(createWithdrawRecord.getTransactionId());
            blockchainDTO.setResult(new String(result));

        } catch (EndorseException endorseException) {
            endorseException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
            return blockchainDTO;
        } catch (SubmitException submitException) {
            submitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
            return blockchainDTO;
        }

        return blockchainDTO;
    }

    @Override
    public BlockchainDTO updateWithDrawRecordStatus(String withdrawKey, String newStatus) {
        BlockchainDTO blockchainDTO = new BlockchainDTO();

        try {
            // TODO 这个方法执行失败了
            byte[] result = contract.submitTransaction("auditWithDrawRecord", withdrawKey, newStatus);
            System.out.println(new String(result));
            blockchainDTO.setResult(new String(result));
        } catch (EndorseException endorseException) {
            endorseException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
            return blockchainDTO;
        } catch (SubmitException submitException) {
            submitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
            return blockchainDTO;
        } catch (CommitStatusException commitStatusException) {
            commitStatusException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.CommitStatusException.getDis());
            return blockchainDTO;
        } catch (CommitException commitException) {
            commitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.CommitException.getDis());
            return blockchainDTO;
        }
        return blockchainDTO;
    }
}
