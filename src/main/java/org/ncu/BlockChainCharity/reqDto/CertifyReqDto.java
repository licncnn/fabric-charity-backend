package org.ncu.BlockChainCharity.reqDto;

import lombok.Data;

@Data
public class CertifyReqDto {
    String idNumber;
    String address;
    String introduction;
    String certificationPath;
    String  logoPath;
}
