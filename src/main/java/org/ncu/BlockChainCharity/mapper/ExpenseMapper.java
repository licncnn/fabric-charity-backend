package org.ncu.BlockChainCharity.mapper;


import org.ncu.BlockChainCharity.bean.Expense;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExpenseMapper {
    //插入支出
    Integer insertExpense(Expense expense);
    //更新支出
    Integer updateExpense(@Param("expenseId")Integer expenseId, @Param("expenseKey")String expenseKey);


    Integer updateExpenseTransaction(@Param("expenseId")Integer expenseId, @Param("transactionId")String transactionId);

    String getImagePathByExpenseKey(@Param("expenseKey")String expenseKey);

    String getTransactionIdByExpenseKey(@Param("expenseKey")String expenseKey);

}
