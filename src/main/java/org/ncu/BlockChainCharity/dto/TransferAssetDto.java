package org.ncu.BlockChainCharity.dto;

import lombok.Data;

@Data
public class TransferAssetDto {
    String[] assetKeys;
    String to;
}
