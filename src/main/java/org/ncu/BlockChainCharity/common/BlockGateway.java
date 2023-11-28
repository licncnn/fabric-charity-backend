//package org.CSLab.demo.common;
//
//import io.grpc.ManagedChannel;
//import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
//import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.hepeng.hyperledgerfabric.app.javademo.HyperLedgerFabricProperties;
//import org.hyperledger.fabric.client.CallOption;
//import org.hyperledger.fabric.client.Network;
//import org.hyperledger.fabric.client.identity.Identities;
//import org.hyperledger.fabric.client.identity.Signers;
//import org.hyperledger.fabric.client.identity.X509Identity;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//import io.grpc.ManagedChannel;
//import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
//import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.hyperledger.fabric.client.CallOption;
//import org.hyperledger.fabric.client.Contract;
//import org.hyperledger.fabric.client.Gateway;
//import org.hyperledger.fabric.client.Network;
//import org.hyperledger.fabric.client.identity.Identities;
//import org.hyperledger.fabric.client.identity.Signers;
//import org.hyperledger.fabric.client.identity.X509Identity;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.PrivateKey;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.concurrent.TimeUnit;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.PrivateKey;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.concurrent.TimeUnit;
//
//public class BlockGateway {
//    static {
//        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
//    }
//
//    static public Contract getContract() throws IOException {
//        Path walletPath = Paths.get("../","wallet");
//        Path networkConfigPath = Paths.get("..","..","first-network","connection-org1.yaml");
//        Wallet wallet;
//        wallet = Wallet.createFileSystemWallet(walletPath);
//        org.hyperledger.fabric.gateway.Gateway.Builder builder = org.hyperledger.fabric.gateway.Gateway.createBuilder();
//        builder.identity(wallet,"user1").networkConfig(networkConfigPath).discovery(true);
//        Gateway gateway = builder.connect();
//        Network network = gateway.getNetwork("mychannel");
//        Contract contract = network.getContract("fabcar");
//        return contract;
//    }
//
//}
//
//
//
//
//
