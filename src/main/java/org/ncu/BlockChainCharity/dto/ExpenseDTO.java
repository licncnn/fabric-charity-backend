package org.ncu.BlockChainCharity.dto;

import lombok.Data;
import org.ncu.BlockChainCharity.bean.Expense;

@Data
public class ExpenseDTO {
    Expense expense;
    String error;
    String result;
}
