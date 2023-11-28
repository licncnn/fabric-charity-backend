package org.ncu.BlockChainCharity.service;

import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.WithdrawDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface WithDrawService {

    public BlockchainDTO getAllWithDrawRecords() throws Exception;

    public BlockchainDTO addWithDrawRecord(WithdrawDTO withdrawDTO) throws Exception;

    BlockchainDTO updateWithDrawRecordStatus(String withdrawKey, String newStatus);
}
