package org.ncu.BlockChainCharity.bean;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class Contribution {
    private Integer contributionId;
    private Integer userId;
    private String projectKey;
    private String contributionKey;
    private double amount;
    private String transactionId="";
    private Timestamp createTime;
}
