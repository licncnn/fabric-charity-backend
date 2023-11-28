package org.ncu.BlockChainCharity.utils;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.utils.ent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DetectDataTampering {

    @Autowired
    private Gateway gateway;

    @Autowired
    private Contract contract;

    // 存放输出结果
    final ArrayList<String[]> resArr = new ArrayList<>();

    public ResponseEntity detectDataTampering() {
        try {
            // 获取通道对象
            Network network = gateway.getNetwork("hyperledger-fabric-java");
            final Networks networks = new Networks("mychannel");

            // 获取链的块高度
            long blockHeight = networks.getBlockchainInfo().getHeight();;

            // 遍历每个块以检查数据篡改
            for (long i = 1; i < blockHeight; i++) {
                Block block = networks.getBlockByNumber(i);

                // 从块中获取交易信封信息
                List<Block.TransactionEnvelopeInfo> envelopeInfos = block.getTransactionEnvelopeInfos();
                for (Block.TransactionEnvelopeInfo envelopeInfo : envelopeInfos) {
                    if (envelopeInfo.getType() == Block.TransactionEnvelopeInfo.Type.TRANSACTION_ENVELOPE) {
                        Block.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo = envelopeInfo.getTransactionActionInfo();
                        String transactionId = transactionActionInfo.getTransactionId();

                        // 从块中获取交易响应
                        List<Peer.ProcessedTransaction> processedTransactions = networks.getTransactionsByTransactionID(transactionId);
                        // 从交易响应中获取世界状态数据
                        for (Peer.ProcessedTransaction processedTransaction : processedTransactions) {
                            ChaincodeResponse response = (ChaincodeResponse) processedTransaction.getTransactionActionInfo();
                            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                                String worldStateData = new String(response.getResponse().getValue());
                                JsonObject jsonObj= new JsonParser().parse(worldStateData).getAsJsonObject();
                                Set<Map.Entry<String, JsonElement>> entries = jsonObj.entrySet();

                                // 通过与智能合约中的当前世界状态数据进行比较来检测数据篡改
                                for (Map.Entry<String, JsonElement> entry : entries) {
                                    String key = entry.getKey();
                                    String value = entry.getValue().toString();

                                    // 查询智能合约中的当前世界状态数据
                                    String queryResult = new String(contract.evaluateTransaction("query", key));
                                    if (!queryResult.equals(value)) {
                                        // 在此处理被篡改的数据
                                        String[] strings = {key,value};
                                        resArr.add(strings);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("检测数据篡改失败：" + e);
            System.exit(1);
        }
        final ResponseEntity responseEntity = new ResponseEntity(200, "\"数据篡改检测到在块 \" + i + \"，键 \" + key", resArr);
        return responseEntity;
    }
}

