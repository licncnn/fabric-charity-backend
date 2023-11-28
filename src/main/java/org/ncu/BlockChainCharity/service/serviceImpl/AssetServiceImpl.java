package org.ncu.BlockChainCharity.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.hyperledger.fabric.client.*;
import org.ncu.BlockChainCharity.bean.Asset;
import org.ncu.BlockChainCharity.common.ContractErrorMessage;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.mapper.AssetMapper;
import org.ncu.BlockChainCharity.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class AssetServiceImpl implements AssetService {
    @Autowired
    final Gateway gateway;

    @Autowired
    final Contract contract;

    @Autowired
    AssetMapper assetMapper;

    @Override
    public ArrayList<Asset> getAllAssetsByUserId(String uid) throws IOException {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        final ArrayList<Asset> assets = assetMapper.selectAssetByUid(uid);
        if (assets==null){
            blockchainDTO.setError("上传资金数据失败");
            return null;
        }

//        final String s = Arrays.toString(assets.toArray());
//        blockchainDTO.setResult(s);

        return assets;
    }

    @Override
    public ArrayList<Asset> getAllAssetsByProjectKey(String projectKey) {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        final ArrayList<Asset> assets = assetMapper.selectAssetByprojectKey(projectKey);

        if (assets==null){
            blockchainDTO.setError("上传资金数据失败");
            return null;
        }
        final String s = Arrays.toString(assets.toArray());
        blockchainDTO.setResult(s);

        return assets;
    }

    @Override
    public ArrayList<Asset> getAllAssetsNotSpentByProjectKey(String projectKey) {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        final ArrayList<Asset> assets = assetMapper.selectAssetNotSpentByprojectKey(projectKey);

        if (assets==null){
            blockchainDTO.setError("上传资金数据失败");
            return null;
        }
        return assets;
    }

    @Override
    public BlockchainDTO queryAssetTransferTransactions(String assetKey) {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        byte[] result;
        try {
            final SubmittedTransaction createWithdrawRecord = contract.newProposal("getTransactionsForAsset")
                    .addArguments(assetKey)
                    .build()
                    .endorse()
                    .submitAsync();
            result = createWithdrawRecord.getResult();
            blockchainDTO.setTransactionId(createWithdrawRecord.getTransactionId());
            blockchainDTO.setResult(new String(result));
        } catch (EndorseException endorseException) {
            endorseException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
            return blockchainDTO;
        } catch (SubmitException submitException) {
            submitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
            return blockchainDTO;
        }

        return blockchainDTO;
    }


    // 创建资产 先插入数据库 再上传到合约中
    @Override
    public BlockchainDTO createAsset(String projectKey, String uid, String value, String to) {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        Asset asset = new Asset();
        asset.setProjectKey(projectKey);
        asset.setUid(uid);
        asset.setValue(value);

        // fix  withdrawKey
        if (assetMapper.insertAsset(asset)==0){
            blockchainDTO.setError("上传资金数据失败");
            return blockchainDTO;
        }
        // fix
        final Integer assetId = asset.getAssetId();
        String assetKey = String.format("ASSET%03d",assetId);

        if(assetMapper.updateAssetKeyBywithdrawId(assetId,assetKey)==0){
            blockchainDTO.setError("更新提现记录失败");
            return blockchainDTO;
        }

        // 上传到区块链中
        byte[] result;
        try {
            final String time = new Timestamp(System.currentTimeMillis()).toString();
            final SubmittedTransaction createAssetTransaction = contract.newProposal("createAsset")
                    .addArguments(assetKey, projectKey, "用户uid："+uid, value,"慈善组织："+to, time)
                    .build()
                    .endorse()
                    .submitAsync();

            System.out.println("交易id==>"+createAssetTransaction.getTransactionId());

            asset.setTransactionId1(createAssetTransaction.getTransactionId());

            if(assetMapper.updateAssetTransaction1(assetKey,createAssetTransaction.getTransactionId())==0){
                blockchainDTO.setError("上传出错");
                return blockchainDTO;
            }

            result = createAssetTransaction.getResult();
            blockchainDTO.setTransactionId(createAssetTransaction.getTransactionId());
            blockchainDTO.setResult(new String(result));
        } catch (EndorseException endorseException) {
            endorseException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
            return blockchainDTO;
        } catch (SubmitException submitException) {
            submitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
            return blockchainDTO;
        }


        return blockchainDTO;
    }

    @Override
    public BlockchainDTO transferAsset(String[] assetKeys, String to) {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        // 更改has_spent

        final String time = new Timestamp(System.currentTimeMillis()).toString();
        for(String assetKey:assetKeys){
            byte[] result;
            try {
                final SubmittedTransaction createAssetTransaction = contract.newProposal("transferAsset")
                        .addArguments(assetKey, to, time)
                        .build()
                        .endorse()
                        .submitAsync();

                System.out.println("交易id==>"+createAssetTransaction.getTransactionId());
                if(assetMapper.updateAssetTransaction2(assetKey,createAssetTransaction.getTransactionId())==0){
                    blockchainDTO.setError("上传出错");
                    return blockchainDTO;
                }
                // 已花费
                assetMapper.updateAssetSpentStatus(assetKey,"1");


                result = createAssetTransaction.getResult();
                blockchainDTO.setTransactionId(createAssetTransaction.getTransactionId());
                blockchainDTO.setResult(new String(result));
            } catch (EndorseException endorseException) {
                endorseException.printStackTrace();
                blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
                return blockchainDTO;
            } catch (SubmitException submitException) {
                submitException.printStackTrace();
                blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
                return blockchainDTO;
            }

        }

        // 调用合约

        return blockchainDTO;
    }


}
