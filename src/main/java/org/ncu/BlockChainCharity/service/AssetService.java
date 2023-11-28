package org.ncu.BlockChainCharity.service;

import org.ncu.BlockChainCharity.bean.Asset;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;

@Service
public interface AssetService {
    public ArrayList<Asset> getAllAssetsByUserId(String uid) throws IOException;

    public ArrayList<Asset> getAllAssetsByProjectKey(String projectKey);
    public ArrayList<Asset> getAllAssetsNotSpentByProjectKey(String projectKey);

    public BlockchainDTO queryAssetTransferTransactions(String assetKey);
    public  BlockchainDTO createAsset(String projectKey,String uid,String value,String to);
    BlockchainDTO transferAsset(String[] assetKeys, String to);
}
