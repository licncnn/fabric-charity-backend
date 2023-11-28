# 工程简介

} catch (EndorseException endorseException) {
   endorseException.printStackTrace();
   blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
   return blockchainDTO;
} catch (SubmitException submitException) {
   submitException.printStackTrace();
   blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
   return blockchainDTO;
} catch (CommitStatusException commitStatusException) {
   commitStatusException.printStackTrace();
   blockchainDTO.setError(ContractErrorMessage.CONTRACT.getDis());
   return blockchainDTO;
} catch (GatewayException contractException){
   blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
   contractException.printStackTrace();
} catch (CommitException commitException) {
   commitException.printStackTrace();
   blockchainDTO.setError(ContractErrorMessage.CommitException.getDis());
   return blockchainDTO;
}


## 参考：

连接配置文件: https://hyperledger-fabric.readthedocs.io/zh_CN/latest/developapps/connectionprofile.html

https://github.com/hyperledger/fabric-gateway-java

## connection.json 配置文件参考
https://github.com/hyperledger/fabric-gateway-java/blob/main/src/test/java/org/hyperledger/fabric/gateway/connection-tls.json

## 博客参考

用fabric-sdk-java调用Fabcar链码,搭建自己的应用程序: https://blog.csdn.net/qq_42482245/article/details/112530995
利用fabric-gateway-java连接并调用fabric链码：https://blog.csdn.net/klay077/article/details/109189630

智能合约实践-设置背书策略：https://blog.csdn.net/qq_28540443/article/details/104379132

## 应用中访问的合约代码

https://gitee.com/kernelHP/hyperledger-fabric-contract-java-demo

# 延伸阅读

## 应用 & 合约 & fabric 网络的关系
![](RunningtheSample.png)



# 基于区块链的慈善募捐平台
## 开发框架：
- Spring-Boot:2.4.11
- MyBatis:3.5.3

## 数据库：
- MySQL:8.0.23

## 区块链：
- Hyperledger fabric 1.4.4

环境：

- docker：19.03.9
- docker-compose:1.18.0

## 链上实体
- Project 项目
- Contribution 捐赠
- Expense 支出
- Admin 管理员
- Charity 机构
- User 用户

### Project
- projectId 项目id
- projectName 项目名
- charityId 发起机构的id
- projectKey 项目在链上对应key
- comment 项目描述
- target 筹款目标
- current 当前金额
- status 项目状态(进行中/中止/未审核) 0->未上链  1->已上链进行中...
- materialPath 项目证明资料的存储路径
- endTime 终止时间
- createTime 创建时间
### Contribution
- contributionId 捐款Id
- userId 捐款用户的Id
- projectKey 受捐项目的key
- contributionKey 捐款的key
- amount 金额
- createTime 创建时间
### Expense
- expenseId 支出Id
- expenseKey 支出的key
- projectKey 支出对应的项目的key
- amount 支出金额
- comment 描述
- createTime 创建时间

## 上链过程
### Project
1. 认证过的机构上传项目资料，先存入数据库等待审核
2. 管理员对未审核的机构进行审核，审核通过后，将项目信息上链。
### Expense
1. 机构提交支出
2. 支出信息上链同时存入数据库
3. 更新项目(链上)的支出金额
### Contribution
1. 用户提交捐款信息
2. 捐款数据上链，同时存入数据库
3. 更新项目已筹得金额


待解决问题：

查询慢，可能跟我服务器的性能有关

增加一个监听，返回数据上链的区块信息。

改进想法：
1. 捐款与支出对应，机构一次拿走一些捐款的金作为支出，
   同时修改捐款信息，达到溯源，直接放弃Expense实体。
2. 项目直接上链，抛弃数据库


账号问题
不要说没有账号，给机构账号，直观看出机构有多少钱，互相监督。# fabric-charity-backend
# fabric-charity-backend
