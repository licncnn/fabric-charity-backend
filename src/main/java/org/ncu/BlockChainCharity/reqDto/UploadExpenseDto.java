package org.ncu.BlockChainCharity.reqDto;


import lombok.Data;

@Data
public class UploadExpenseDto {
    String projectKey;
    double amount;
    String expenseMaterialPath;
    String comment;
}
