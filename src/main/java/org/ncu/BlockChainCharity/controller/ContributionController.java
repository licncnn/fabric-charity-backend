package org.ncu.BlockChainCharity.controller;


import org.ncu.BlockChainCharity.bean.Admin;
import org.ncu.BlockChainCharity.bean.User;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.reqDto.UploadDto;
import org.ncu.BlockChainCharity.service.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("contribution")
public class ContributionController {

    @Autowired
    ContributionService contributionService;


    @ResponseBody
    @GetMapping("getContribution")
    public ResponseEntity getContribution(String contributionkey) throws Exception{

        BlockchainDTO blockchainDTO = contributionService.getContributionByKey(contributionkey);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }



    @ResponseBody
    @GetMapping("getAllContributions")
    public ResponseEntity getAllContributions() throws Exception{
        BlockchainDTO blockchainDTO = contributionService.getAllContributions();
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }



    @ResponseBody
    @PostMapping("uploadContribution")
    public ResponseEntity uploadContribution(@RequestBody UploadDto uploadDto, HttpSession session) throws Exception {
        // TODO 修改回来
        User user = (User)session.getAttribute("user");
//        BlockchainDTO blockchainDTO = contributionService.addContribution(user.getUserId(),projectKey,amount);
        BlockchainDTO blockchainDTO = contributionService.addContribution(user.getUserId(), uploadDto.getProjectKey(), uploadDto.getAmount());
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    @ResponseBody
    @GetMapping("getContributionsByUserId")
    public ResponseEntity getContributionsByUserId(HttpSession session) throws IOException {
        // TODO 改回来
        User user = (User) session.getAttribute("user");

        BlockchainDTO blockchainDTO = contributionService.getContributionsByUserId(user.getUserId());
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    @ResponseBody
    @GetMapping("getContributionsByProjectKey")
    public ResponseEntity getContributionsByProjectKey(String projectKey) throws IOException {
        BlockchainDTO blockchainDTO = contributionService.getContributionsByProjectKey(projectKey);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }



    @ResponseBody
    @GetMapping("getTransactionIdByContributionKey")
    public ResponseEntity getTransactionIdByContributionKey(String contributionKey) throws IOException {

        BlockchainDTO blockchainDTO = contributionService.getTransactionIdByContibutionKey(contributionKey);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }
}