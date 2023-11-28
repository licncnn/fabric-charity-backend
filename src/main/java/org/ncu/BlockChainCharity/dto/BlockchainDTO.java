package org.ncu.BlockChainCharity.dto;

public class BlockchainDTO {
    public BlockchainDTO(String result, String error) {
        this.result = result;
        this.error = error;
        this.transactionId="";
    }

    public BlockchainDTO(){
    }

    String result;
    String error;
    String transactionId;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
