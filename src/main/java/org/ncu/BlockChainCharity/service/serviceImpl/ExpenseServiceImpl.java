package org.ncu.BlockChainCharity.service.serviceImpl;

import lombok.AllArgsConstructor;
import org.ncu.BlockChainCharity.bean.Expense;
import org.ncu.BlockChainCharity.common.ContractErrorMessage;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.mapper.ExpenseMapper;
import org.ncu.BlockChainCharity.mapper.ProjectMapper;
import org.ncu.BlockChainCharity.service.ExpenseService;
import org.hyperledger.fabric.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;


@Service
@AllArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }
    @Autowired
    final Gateway gateway;

    @Autowired
    final Contract contract;



    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ExpenseMapper expenseMapper;

    @Autowired
    ProjectMapper projectMapper;




    @Override
    public BlockchainDTO getAllExpenses()throws IOException {

        BlockchainDTO blockchainDTO = new BlockchainDTO();
//        Contract contract = BlockGateway.getContract();
        try {
            byte[] result = contract.evaluateTransaction("queryAllExpenses");
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
            blockchainDTO.setError(ContractErrorMessage.CONTRACT.getDis());
            return blockchainDTO;
        } catch (GatewayException contractException){
            blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
            contractException.printStackTrace();
        }

        return blockchainDTO;
    }


    public BlockchainDTO getExpenseByKey(String key)throws IOException{
        BlockchainDTO blockchainDTO = new BlockchainDTO();
//        Contract contract = BlockGateway.getContract();
        try{
            byte[] result = contract.evaluateTransaction("queryAllExpenses",key);
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
            blockchainDTO.setError(ContractErrorMessage.CONTRACT.getDis());
            return blockchainDTO;
        } catch (GatewayException contractException){
            blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
            contractException.printStackTrace();
        }
        return blockchainDTO;
    }


    /**
     * 策略 ： 新增字段  图片地址  交易id
     * 合约字段不变和以前一样上传
     * 数据库中插入 图片地址已经交易id
     * 前段新增两个按钮  点击通过数据库查询链接
     * @param projectKey
     * @param amount
     * @param comment
     * @param expenseMaterialPath
     * @return
     * @throws IOException
     */
    @Override
    public BlockchainDTO addExpense(String projectKey, double amount, String comment,String expenseMaterialPath)throws IOException{

        if(amount>0){
            amount = -1.0 * amount;
        }
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        Expense expense = new Expense();
        expense.setProjectKey(projectKey);
        expense.setAmount(amount);// -200.5
        expense.setComment(comment);
        // **new add material path
        expense.setExpenseMaterialPath(expenseMaterialPath);
        expense.setCreateTime(new Timestamp(System.currentTimeMillis()));
        // 先把Expense插入数据库
        if(expenseMapper.insertExpense(expense)==0){
            logger.error("上传支出数据失败");
            blockchainDTO.setError("上传支出数据失败");
            return blockchainDTO;
        }
        //获取主键，用主键生成key
        Integer expenseId = expense.getExpenseId();
        String expenseKey = String.format("EXPENSE%03d",expenseId);

        expense.setExpenseKey(expenseKey);
        if(expenseMapper.updateExpense(expenseId,expenseKey)==0){
            logger.error("更新项目键值失败");
            blockchainDTO.setError("上传出错");
            return blockchainDTO;
        }
//         current 是项目当前的欠款  逻辑是存款到达target之后才开始花费
        if(projectMapper.updateProjectCurrent(projectKey,amount)==0){
            logger.error("更新数据库中项目余额出错");
            blockchainDTO.setError("上传出错");
        }
        // 总支出 应当为正数
//        System.out.println(-1*amount);
        if(projectMapper.updateProjectTotalExpense(projectKey,-1*amount)==0){
            logger.error("更新数据库中项目总支出额出错");
            blockchainDTO.setError("上传出错");
        }

        //TODO 更新project的余额

//        //将Expense上链
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        byte[] result;
        String charityKey = projectMapper.getCharityKeyByProjectKey(projectKey);

        try{

            final SubmittedTransaction createExpense = contract.newProposal("createExpense")
                .addArguments(expenseKey,projectKey,charityKey,amount+"",comment,currentTime+"")
                .build()
                .endorse()
                .submitAsync();

            System.out.println("交易id==>"+createExpense.getTransactionId());

            expense.setTransactionId(createExpense.getTransactionId());
            if(expenseMapper.updateExpenseTransaction(expenseId,createExpense.getTransactionId())==0){
                logger.error("更新项目键值失败");
                blockchainDTO.setError("上传出错");
                return blockchainDTO;
            }

            result = createExpense.getResult();

//            result = contract.submitTransaction("createExpense", expenseKey,projectKey,charityKey,amount+"",comment,currentTime+"");
            blockchainDTO.setTransactionId(createExpense.getTransactionId());

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
    public BlockchainDTO getExpensesByProjectKey(String projectKey) throws IOException {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
//        Contract contract = BlockGateway.getContract();
        try{
            byte[] result = contract.evaluateTransaction("queryExpensesByProjectKey",projectKey);
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
        return blockchainDTO;
    }

    @Override
    public BlockchainDTO getImagePathByExpenseKey(String expenseKey) throws IOException {
        final String imagePathByExpenseKey = expenseMapper.getImagePathByExpenseKey(expenseKey);
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        blockchainDTO.setResult(imagePathByExpenseKey);

        return blockchainDTO;
    }

    @Override
    public BlockchainDTO getTransactionIdByExpenseKey(String expenseKey) throws IOException {
        final String transactionId = expenseMapper.getTransactionIdByExpenseKey(expenseKey);
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        blockchainDTO.setResult(transactionId);
        return blockchainDTO;
    }

}
