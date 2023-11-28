package org.ncu.BlockChainCharity.dto;

import lombok.Data;

@Data
public class CharityDTO {
    String charityKey;
    String charityName;
    int isCertified;
    int isAudited;
    String email;
    String address;
    String introduction;
    String certificationPath;
    String logoPath;

}
