package org.ncu.BlockChainCharity.controller;


import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.reqDto.UploadExpenseDto;
import org.ncu.BlockChainCharity.service.ExpenseService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("expense")
public class ExpenseController{

    @Autowired
    ExpenseService expenseService;

    @ResponseBody
    @GetMapping("getAllExpenses")
    public ResponseEntity getAllExpenses()throws Exception{
        BlockchainDTO blockchainDTO =  expenseService.getAllExpenses();
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    @ResponseBody
    @GetMapping("getExpense")
    public ResponseEntity getExpense(String key) throws Exception{

        BlockchainDTO blockchainDTO = expenseService.getExpenseByKey(key);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    // amount 传入负数
    @ResponseBody
    @PostMapping("uploadExpense")
    public ResponseEntity uploadExpense(@RequestBody UploadExpenseDto uploadExpenseDto) throws Exception {

        // 最好的方法是从session 中获取charityKey
        BlockchainDTO blockchainDTO = expenseService.addExpense(uploadExpenseDto.getProjectKey(),uploadExpenseDto.getAmount(),uploadExpenseDto.getComment(),uploadExpenseDto.getExpenseMaterialPath());
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult(),blockchainDTO.getTransactionId());
    }


    @ResponseBody
    @GetMapping("getExpensesByProjectKey")
    public ResponseEntity getExpensesByProjectKey(String projectKey) throws IOException {
//        System.out.println("getExpensesByProjectKey invoke");
//        System.out.println(projectKey);
        BlockchainDTO blockchainDTO = expenseService.getExpensesByProjectKey(projectKey);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    @ResponseBody
    @GetMapping("getTransactionIdByExpenseKey")
    public ResponseEntity getTransactionIdByExpenseKey(String expenseKey) throws IOException {
//        System.out.println("getExpensesByProjectKey invoke");
//        System.out.println(projectKey);
        BlockchainDTO blockchainDTO = expenseService.getTransactionIdByExpenseKey(expenseKey);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    @ResponseBody
    @GetMapping("getImagePathByExpenseKey")
    public ResponseEntity getImagePathByExpenseKey(String expenseKey) throws IOException {
//        System.out.println("getExpensesByProjectKey invoke");
//        System.out.println(projectKey);

        System.out.println(expenseKey);


        BlockchainDTO blockchainDTO = expenseService.getImagePathByExpenseKey(expenseKey);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }

}