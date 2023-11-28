package org.ncu.BlockChainCharity.dto;


import lombok.Data;

@Data
public class AddAssetDto {
    String projectKey;
    String value;
    String to;
}
