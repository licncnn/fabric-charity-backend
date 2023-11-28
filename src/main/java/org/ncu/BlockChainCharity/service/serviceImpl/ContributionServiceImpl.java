package org.ncu.BlockChainCharity.service.serviceImpl;

import lombok.AllArgsConstructor;
import org.ncu.BlockChainCharity.bean.Contribution;
import org.ncu.BlockChainCharity.common.ContractErrorMessage;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.mapper.ContributionMapper;
import org.ncu.BlockChainCharity.mapper.ProjectMapper;
import org.ncu.BlockChainCharity.service.ContributionService;

import org.hyperledger.fabric.client.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;


@Service
@AllArgsConstructor
public class ContributionServiceImpl implements ContributionService {

    @Autowired
    Logger logger;

    @Autowired
    final Gateway gateway;

    @Autowired
    final Contract contract;


    @Autowired
    ContributionMapper contributionMapper;

    @Autowired
    ProjectMapper projectMapper;


    public BlockchainDTO getAllContributions() throws IOException {
        BlockchainDTO blockchainDTO = new BlockchainDTO();

        try{
            byte[] result = contract.evaluateTransaction("queryAllContributions");
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
        } catch (GatewayException contractException){
            blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
            contractException.printStackTrace();
        }
        return blockchainDTO;
    }


    @Override
    public BlockchainDTO getContributionByKey(String key)throws IOException{
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        try {
            byte[] result = contract.evaluateTransaction("queryContribution", key);
            blockchainDTO.setResult(new String(result));
//        } catch (ContractException contractException) {
//            logger.error("合约运行异常，getContributionByKey 执行失败");
//            blockchainDTO.setError(ContractErrorMessage.CONTRACT.getDis());
//        }
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


    // 这个amount 是正数
    @Override
    public BlockchainDTO addContribution(Integer userId,String projectKey, double amount)throws IOException{
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        Contribution contribution = new Contribution();

        contribution.setUserId(userId);
        contribution.setAmount(amount);
        contribution.setProjectKey(projectKey);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        contribution.setCreateTime(timestamp);

        if(contributionMapper.insertContribution(contribution)==0){
            logger.error("上传捐款数据失败");
            blockchainDTO.setError("上传捐款数据失败");
            return blockchainDTO;
        }
        Integer contributionId = contribution.getContributionId();
        String contributionKey = String .format("CONTRIBUTION%03d",contributionId);
        if(contributionMapper.updateContribution(contributionId,contributionKey)==0){
            logger.error("更新捐款键值失败");
            blockchainDTO.setError("更新捐款键值失败");
            return blockchainDTO;
        }
        if(projectMapper.updateProjectCurrent(projectKey,amount)==0){
            logger.error("更新项目已筹款额出错");
            blockchainDTO.setError("更新项目已筹款额出错");
        }
        // TODO
        String charityKey = projectMapper.getCharityKeyByProjectKey(projectKey);

        byte[] result;
        try {
            final SubmittedTransaction createContribution = contract.newProposal("createContribution")
                    .addArguments(contributionKey, userId + "", projectKey, charityKey, amount + "", timestamp + "")
                    .build()
                    .endorse()
                    .submitAsync();

            System.out.println("交易id==>"+createContribution.getTransactionId());

            contribution.setTransactionId(createContribution.getTransactionId());

            if(contributionMapper.updateContributionTransaction(contributionId,createContribution.getTransactionId())==0){
                logger.error("更新项目键值失败");
                blockchainDTO.setError("上传出错");
                return blockchainDTO;
            }
            result = createContribution.getResult();
            blockchainDTO.setTransactionId(createContribution.getTransactionId());

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
    public BlockchainDTO getContributionsByUserId(Integer userId) throws IOException {

        BlockchainDTO blockchainDTO = new BlockchainDTO();

//        Contract contract = BlockGateway.getContract();
        try{
            byte[] result = contract.evaluateTransaction("queryContributionsByUserId",userId+"");
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
        } catch (GatewayException contractException){
            blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
            contractException.printStackTrace();
        }
//        catch (ContractException contractException) {
//            logger.error("合约执行异常，queryContributionsByUserId 执行失败");
//            blockchainDTO.setError(ContractErrorMessage.CONTRACT.getDis());
//        }
        return blockchainDTO;
    }


    @Override
    public BlockchainDTO getContributionsByProjectKey(String projectKey) throws IOException {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
//        Contract  contract = BlockGateway.getContract();
        try{
            byte[] result = contract.evaluateTransaction("queryContributionsByProjectKey",projectKey);
            blockchainDTO.setResult(new String (result));
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
//        catch (ContractException contractException) {
//            logger.error("合约运行异常，getExpensesByProjectKey执行失败 ");
//            blockchainDTO.setError(ContractErrorMessage.CONTRACT.getDis());
//        }
        return blockchainDTO;
    }

    @Override
    public BlockchainDTO getTransactionIdByContibutionKey(String contributionKey) throws IOException {
        final String transactionId = contributionMapper.getTransactionIdByContributionKey(contributionKey);
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        blockchainDTO.setResult(transactionId);
        return blockchainDTO;
    }

}
