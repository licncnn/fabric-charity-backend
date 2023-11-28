package org.ncu.BlockChainCharity.dto;

import lombok.Data;
import org.ncu.BlockChainCharity.bean.Contribution;

@Data
public class ContributionDTO {
    Contribution contribution;
    String error;
    String result;
}
