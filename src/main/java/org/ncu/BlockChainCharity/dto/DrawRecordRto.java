package org.ncu.BlockChainCharity.dto;

import lombok.Data;

@Data
public class DrawRecordRto {
    private String _id;
    private String _rev;
    private String amount;
    private String bank_id_card;
    private String bank_name;
    private String charityKey;
    private String comment;
    private String datetime;
    private String id_card;
    private String name;
    private String projectKey;
    private String publicKey;
    private String signature;
    private String state;
    private String type;
    private String withdrawKey;
    // 省略 getter 和 setter 方法
}