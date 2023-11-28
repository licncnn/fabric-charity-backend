package org.ncu.BlockChainCharity.service;

import org.ncu.BlockChainCharity.dto.BlockchainDTO;

import java.io.IOException;

public interface ExpenseService {
    public BlockchainDTO getAllExpenses() throws IOException;

    public BlockchainDTO getExpenseByKey(String key) throws IOException;
    public BlockchainDTO addExpense(String projectKey,double amount,String comment,String expenseMaterialPath) throws IOException;

    public BlockchainDTO getExpensesByProjectKey(String projectKey) throws IOException;




    public BlockchainDTO getImagePathByExpenseKey(String expenseKey) throws IOException;

    public BlockchainDTO getTransactionIdByExpenseKey(String expenseKey) throws IOException;

}
