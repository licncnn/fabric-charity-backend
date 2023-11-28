package org.ncu.BlockChainCharity.bean;

import com.google.type.DateTime;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class WithdrawRecord{
    Integer withdrawId;
    String withdrawKey;
    String charityKey; // operator
    String projectKey;
    Double amount;
    String bank_name;  //加密
    String bank_id_card; // 加密
    String name; // 加密
    String id_card; // 加密
    String comment;
    Timestamp datetime;
    int state=0;  // 状态 0 未审核  1 已审核
    private String transactionId="";
}