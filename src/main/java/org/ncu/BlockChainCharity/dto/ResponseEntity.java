package org.ncu.BlockChainCharity.dto;

import java.util.Map;

public class ResponseEntity {
    int code;
    String msg;
    Object data;
    String transactionId;

    public ResponseEntity(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseEntity(int code, String msg, Object data,String transactionId) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.transactionId = transactionId;
    }




    public ResponseEntity(){
    }

    public String getTransactionId () {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
