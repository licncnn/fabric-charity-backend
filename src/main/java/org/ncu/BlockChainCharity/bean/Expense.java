package org.ncu.BlockChainCharity.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Expense {
    private Integer expenseId;
    private String expenseKey;
    private String projectKey;
    private double amount;
    private String comment;
    private String expenseMaterialPath;
    private String transactionId="";
    Timestamp createTime;

}