package org.ncu.BlockChainCharity.mapper;


import org.ncu.BlockChainCharity.bean.Charity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CharityMapper  {
    //根据机构名查询机构
    Charity getCharityByCharityName(@Param("charityName")String charityName);

    Charity getCharityByCharityId(@Param("carityId")Integer carityId);
    Charity getCharityByCharityKey(@Param("carityKey")String carityKey);


    Integer certify(@Param("charityId")Integer charityId,@Param("idNumber")String idNumber,@Param("address")String address, @Param("introduction")String introduction, @Param("certificationPath")String certificationPath, @Param("logoPath")String logoPath);
    Integer audit(@Param("charityId")Integer charityId,@Param("auditResult")Integer auditResult);
    //查询待审核机构
    List<Charity> getUnderAuditedCharity();

    //插入机构
    void insertCharity(Charity charity);

    Integer updateCharityKey(@Param("charityId")Integer charityId, @Param("charityKey")String charityKey);
}