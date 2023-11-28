package org.ncu.BlockChainCharity.dto;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class WithdrawDTO {
    String charityKey; // operator
    String projectKey;
    Double amount;
    String bank_name;  //加密
    String bank_id_card; // 加密
    String name; // 加密
    String id_card; // 加密
    String comment;
}
