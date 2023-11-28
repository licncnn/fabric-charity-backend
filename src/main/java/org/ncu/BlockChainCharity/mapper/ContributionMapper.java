package org.ncu.BlockChainCharity.mapper;

import org.ncu.BlockChainCharity.bean.Contribution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ContributionMapper {
    //插入捐款
    int insertContribution(Contribution contribution);
    //更新捐款
    int updateContribution(@Param("contributionId")Integer contributionId,@Param("contributionKey")String contributionKey);



    Integer updateContributionTransaction(@Param("contributionId")Integer contributionId, @Param("transactionId")String transactionId);

    String getTransactionIdByContributionKey(@Param("contributionKey")String contributionKey);

}
