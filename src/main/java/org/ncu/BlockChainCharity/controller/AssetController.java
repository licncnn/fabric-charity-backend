package org.ncu.BlockChainCharity.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.ncu.BlockChainCharity.bean.Asset;
import org.ncu.BlockChainCharity.bean.Charity;
import org.ncu.BlockChainCharity.bean.User;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.dto.AddAssetDto;
import org.ncu.BlockChainCharity.dto.TransferAssetDto;
import org.ncu.BlockChainCharity.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("asset")
public class AssetController {
    @Autowired
    AssetService assetService;

    //管理员提交审核受否通过
    @ResponseBody
    @PostMapping("addAsset")
    public ResponseEntity addAsset(@RequestBody AddAssetDto addAssetDto, HttpSession session){
        User user = (User)session.getAttribute("user");
//        System.out.println(user);
        final BlockchainDTO blockchainDTO = assetService.createAsset(addAssetDto.getProjectKey(), user.getUserId()+"", addAssetDto.getValue(),addAssetDto.getTo());

        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    @ResponseBody
    @PostMapping("transferAsset")
    public ResponseEntity transferAsset(@RequestBody TransferAssetDto transferAssetDto){
        final BlockchainDTO blockchainDTO = assetService.transferAsset(transferAssetDto.getAssetKeys(),transferAssetDto.getTo());

        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    //获取待审核机构
    @ResponseBody
    @GetMapping("queryAssetByUid")
    public ResponseEntity queryAssetByUid(String uid) throws IOException {
//        User user = (User)session.getAttribute("user");
        final ArrayList<Asset> assets = assetService.getAllAssetsByUserId(uid);
        if (assets==null){
            return new ResponseEntity(400,"获取用户资产信息出错",null);
        }
        return new ResponseEntity(200,"success",JSON.toJSON(assets));
    }

    //获取待审核机构
    @ResponseBody
    @GetMapping("queryAssetByProjectKey")
    public ResponseEntity queryAssetAllByProjectKey(String projectKey){
        final ArrayList<Asset> assets = assetService.getAllAssetsByProjectKey(projectKey);
        if (assets==null){
            return new ResponseEntity(400,"获取未审核机构出错",null);
        }
//        String response = JSON.toJSONString(underAuditedCharity, SerializerFeature.PrettyFormat);
        return new ResponseEntity(200,"success",JSON.toJSON(assets));
    }


    //获取待审核机构
    @ResponseBody
    @GetMapping("queryAssetNotSpentByProjectKey")
    public ResponseEntity queryAssetNotSpentByProjectKey(String projectKey){
        final ArrayList<Asset> assets = assetService.getAllAssetsNotSpentByProjectKey(projectKey);
        if (assets==null){
            return new ResponseEntity(400,"获取未审核机构出错",null);
        }
//        String response = JSON.toJSONString(underAuditedCharity, SerializerFeature.PrettyFormat);
        return new ResponseEntity(200,"success",JSON.toJSON(assets));
    }

    //获取待审核机构
    @ResponseBody
    @GetMapping("queryAssetTransactions")
    public ResponseEntity queryAssetTransactions(String assetKey){
        final BlockchainDTO blockchainDTO = assetService.queryAssetTransferTransactions(assetKey);
        if (blockchainDTO==null){
            return new ResponseEntity(400,"获取用户资产信息出错",null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }

}
