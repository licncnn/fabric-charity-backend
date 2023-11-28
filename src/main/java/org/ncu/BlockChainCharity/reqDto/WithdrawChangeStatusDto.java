package org.ncu.BlockChainCharity.reqDto;

import lombok.Data;

@Data
public class WithdrawChangeStatusDto {
    String withdrawKey;
    String newStatus;
}
