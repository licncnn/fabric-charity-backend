package org.ncu.BlockChainCharity.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ncu.BlockChainCharity.bean.WithdrawRecord;

@Mapper
public interface WithdrawMapper {

    int insertWithDrawRecord(WithdrawRecord withdrawRecord);

    int updateWithDrawRecord(@Param("withdrawId")Integer withdrawId,@Param("withdrawKey")String withdrawKey);

    Integer updateWithdrawRecordTransaction(@Param("withdrawId")Integer withdrawId, @Param("transactionId")String transactionId);
}