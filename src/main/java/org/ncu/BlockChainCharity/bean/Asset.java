package org.ncu.BlockChainCharity.bean;

import lombok.Data;

@Data
public class Asset {
    public Integer assetId;
    public String AssetKey;
    public String projectKey;
    public String uid;
    public String value;
    public String hasspent; // 0 未花费 or 1 已花费
    public String transactionId1="";
    public String transactionId2="";

}