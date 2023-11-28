package org.ncu.BlockChainCharity.controller;

import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.dto.WithdrawDTO;
import org.ncu.BlockChainCharity.reqDto.UploadDto;
import org.ncu.BlockChainCharity.reqDto.WithdrawChangeStatusDto;
import org.ncu.BlockChainCharity.service.AdminService;
import org.ncu.BlockChainCharity.service.WithDrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("withdraw")
public class WithDrawController {
    @Autowired
    WithDrawService withDrawService;


    @ResponseBody
    @PostMapping("addWithDrawRecord")
    public ResponseEntity uploadWithDrawRecord(@RequestBody WithdrawDTO withdrawDTO, HttpSession session) throws Exception {
        // TODO 修改回来

        BlockchainDTO blockchainDTO = withDrawService.addWithDrawRecord(withdrawDTO);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }

    @ResponseBody
    @GetMapping("getAllWithDrawRecords")
    public ResponseEntity getAllWithDrawRecords() throws Exception{
        BlockchainDTO blockchainDTO = withDrawService.getAllWithDrawRecords();
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }

    @ResponseBody
    @GetMapping("changeWithDrawRecordStatus")
    public ResponseEntity changeWithDrawRecordStatus(String withdrawKey) throws IOException {
        BlockchainDTO blockchainDTO = withDrawService.updateWithDrawRecordStatus(withdrawKey, "1");

        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }

}
