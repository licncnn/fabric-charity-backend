package org.ncu.BlockChainCharity.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ncu.BlockChainCharity.bean.Asset;
import org.ncu.BlockChainCharity.bean.Project;
import org.ncu.BlockChainCharity.bean.WithdrawRecord;

import java.util.ArrayList;

@Mapper
public interface AssetMapper {

    int updateAssetSpentStatus(@Param("assetKey")String assetKey,@Param("spentStatus")String spentStatus);

    Integer updateAssetTransaction1(@Param("assetKey")String assetKey, @Param("transactionId")String transactionId);

    Integer updateAssetTransaction2(@Param("assetKey")String assetKey, @Param("transactionId")String transactionId);


    int insertAsset(Asset asset);
    ArrayList<Asset> selectAssetByprojectKey(@Param("projectKey")String projectKey);
    ArrayList<Asset> selectAssetNotSpentByprojectKey(@Param("projectKey")String projectKey);

    ArrayList<Asset> selectAssetByUid(@Param("uid")String uid);
    Integer updateAssetKeyBywithdrawId(@Param("assetId")Integer assetId, @Param("assetKey")String assetKey);
}
