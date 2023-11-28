package org.ncu.BlockChainCharity.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.ncu.BlockChainCharity.dto.RegisterDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.reqDto.RegisterUserDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import com.fasterxml.jackson.databind.*;

@Controller
@RequestMapping("project")
public class TamperingDetect {

    private static final String DB1_URL = "http://127.0.0.1:5984";
    private static final String DB2_URL = "http://127.0.0.1:7984";
    private static final String DB_NAME = "mychannel_hyperledger-fabric-contract-java";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "adminpw";

//    // 指定通道名称和couchdb所在的组织
//    String channelName = "mychannel";
//    String couchdbOrganisation = "Org1MSP";
//
//    // 指定连接的Fabric网络信息
//    Path walletPath = Paths.get("path/to/wallet");
//    Wallet wallet = Wallets.newFileSystemWallet(walletPath);
//    String userName = "user1";
//    String connectionProfilePath = "path/to/connectionProfile.yaml";
//    Gateway.Builder builder = Gateway.createBuilder()
//            .identity(wallet, userName)
//            .networkConfig(Paths.get(connectionProfilePath))
//            .discovery(true);
//    // 连接Fabric网络
//        try (
//    Gateway gateway = builder.connect()) {
//        // 获取通道
//        Network network = gateway.getNetwork(channelName);
//        Channel channel = network.getChannel();
//
//        // 获取通道中的所有区块
//        Collection<BlockInfo> blockInfos = channel.queryBlockchainInfo().getBlockInfos();
//
//        // 遍历所有区块
//        for (BlockInfo blockInfo : blockInfos) {
//            // 遍历区块中所有交易
//            for (TransactionEnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
//                // 获取交易中写入的所有键值对
//                JSONObject keyValuePairs = new JSONObject(envelopeInfo.getChaincodeActionPayload().getAction().getProposalResponsePayload().getExtension().getResponse().getPayload().toByteArray());
//
//                // 检查键值对中是否包含couchdb组织的前缀
//                for (String key : keyValuePairs.keySet()) {
//                    if (key.startsWith(couchdbOrganisation)) {
//                        // 如果包含couchdb组织的前缀，则检查该键值对是否与couchdb中的数据一致
//                        String value = keyValuePairs.getString(key);
//                        String couchdbValue = getCouchDBValue(key);
//                        if (!value.equals(couchdbValue)) {
//                            // 如果键值对不一致，则表示世界状态已被篡改
//                            System.out.println("World state has been tampered with!");
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//        // 如果所有的区块和交易都被遍历完，并且没有检测到世界状态被篡改，则表示世界状态没有被篡改
//        System.out.println("World state has not been tampered with.");
//    }
//}
//
//    // 获取指定键对应的CouchDB中的值
//    private static String getCouchDBValue(String key) {
//        // TODO: 实现获取CouchDB中的值的方法
//        return null;
//    }


    @ResponseBody
    @GetMapping("tamperingDetect")
    public ResponseEntity tamperingDetect(String projectKey) throws IOException {

        // 创建HTTP客户端
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        final ArrayList<String[]> resArr = new ArrayList<>();

        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME, PASSWORD));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();

        // 创建HTTP GET请求
        HttpGet request1 = new HttpGet(DB1_URL + "/" + DB_NAME + "/_all_docs?include_docs=true");
        HttpGet request2 = new HttpGet(DB2_URL + "/" + DB_NAME + "/_all_docs?include_docs=true");


        String auth = USERNAME + ":" + PASSWORD;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
        String authHeader = "Basic " + new String(encodedAuth);
        request1.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        request2.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        // 发送HTTP请求并获取响应
        CloseableHttpResponse response1 = httpClient.execute(request1);
        CloseableHttpResponse response2 = httpClient.execute(request2);

        // 解析响应
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode result1 = objectMapper.readTree(response1.getEntity().getContent());
        JsonNode result2 = objectMapper.readTree(response2.getEntity().getContent());

//        System.out.println(request1);

        System.out.println(result1);
        System.out.println(result2);

        System.out.println("+====================+");
        // 获取每个数据库中的所有记录
        JsonNode rows1 = result1.get("rows");
        JsonNode rows2 = result2.get("rows");

        // 创建两个Map对象，用于存储每个记录的键值对
        Map<String, JsonNode> map1 = new HashMap<>();
        Map<String, JsonNode> map2 = new HashMap<>();

        // 遍历第一个数据库中的所有记录，并将每个记录的键值对存储到map1中
        for (JsonNode row : rows1) {
            JsonNode doc = row.get("doc");
            String key = doc.get("_id").asText();
            map1.put(key, doc);
        }

        // 遍历第二个数据库中的所有记录，并将每个记录的键值对存储到map2中
        for (JsonNode row : rows2) {
            JsonNode doc = row.get("doc");
            String key = doc.get("_id").asText();
            map2.put(key, doc);
        }
        // 对比map1和map2中的每个键值对，如果某个键值对在两个map中都存在，但其值不同，则将该记录打印出来
        for (String key : map1.keySet()) {
            if (map2.containsKey(key)) {
                JsonNode doc1 = map1.get(key);
                JsonNode doc2 = map2.get(key);
                if (!areDocsEqual(doc1, doc2)) {
//                    System.out.println(doc1.toPrettyString());
//                    System.out.println(doc2.toPrettyString());
                    String[] strings = {key,doc1.toString(), doc2.toString()};
                    resArr.add(strings);
                    System.out.println("Record with key " + key + " is different in " + DB1_URL + " and " + DB2_URL);
                }
            }
        }
        // 关闭HTTP客户端和响应
        response1.close();
        response2.close();
        httpClient.close();

//        System.out.println(JSON.toJSON(resArr));

        return new ResponseEntity(200,"检测成功",resArr);
    }

    // 判断两个文档是否相同（除了_rev和~version字段之外的所有字段都相同）
    private static boolean areDocsEqual(JsonNode doc1, JsonNode doc2) {

        // 比较除_rev和~version之外的所有字段
        for (Iterator<String> it = doc1.fieldNames(); it.hasNext(); ) {
            String fieldName = it.next();
            if (!fieldName.equals("_rev") && !fieldName.equals("~version")) {
                JsonNode value1 = doc1.get(fieldName);
                JsonNode value2 = doc2.get(fieldName);
                if (!value1.equals(value2)) {
                    return false;
                }
            }
        }
        return true;
    }



}
