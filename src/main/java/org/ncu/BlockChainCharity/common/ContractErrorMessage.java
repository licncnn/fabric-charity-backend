package org.ncu.BlockChainCharity.common;


public enum ContractErrorMessage {

    CONTRACT("合约运行异常"),
    INTERRUPTED("提交异常中断"),
    EndorseException("区块链节点背书阶段失败,请确保相同的transaction只执行一次"),
    SubmitException("区块链节点提交阶段失败"),
    CommitException("区块链节点共识阶段失败"),
    CommitStatusException("区块链提交状态异常"),
    GatewayException("区块链网关调用失败"),
    TIMEOUT("提交超时");



    private String errorMessage;


    private ContractErrorMessage(String message) {
        this.errorMessage = message;
    }
    public String getDis(){
        return errorMessage;
    }
}