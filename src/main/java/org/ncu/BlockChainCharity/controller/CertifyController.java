package org.ncu.BlockChainCharity.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.ncu.BlockChainCharity.bean.Charity;
import org.ncu.BlockChainCharity.dto.CertifyDTO;
import org.ncu.BlockChainCharity.dto.CharityDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.reqDto.AuditCharityReq;
import org.ncu.BlockChainCharity.reqDto.CertifyReqDto;
import org.ncu.BlockChainCharity.service.AdminService;
import org.ncu.BlockChainCharity.service.CharityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("certify")
public class CertifyController {

    @Autowired
    AdminService adminService;

    @Autowired
    CharityService charityService;


    //机构认证
    // 不需要用Key 直接用Id 就可以了
    @ResponseBody
    @PostMapping("certify")
    public ResponseEntity certify(@RequestBody CertifyReqDto certifyReqDto, HttpSession session){
        // TODO  改回来
        Charity charity = (Charity) session.getAttribute("charity");

        CertifyDTO certifyDTO = charityService.certify(charity.getCharityId(),certifyReqDto.getIdNumber(),certifyReqDto.getAddress(),certifyReqDto.getIntroduction(),certifyReqDto.getCertificationPath(),certifyReqDto.getLogoPath());

        if(certifyDTO.getMessage()==null || certifyDTO.getMessage()==""){
            return new ResponseEntity(400,"审核失败","");
        }

        return new ResponseEntity(200,certifyDTO.getMessage(),"");
//        return certifyDTO.getMessage();
    }

    //管理员提交审核受否通过
    @ResponseBody
    @PostMapping("auditCharity")
    public ResponseEntity auditCharity(@RequestBody AuditCharityReq auditCharityReq){
        System.out.println(auditCharityReq.getCharityId());
        Integer res = adminService.auditCharity(auditCharityReq.getCharityId());// isAudit

        if (res==1){
            return new ResponseEntity(200,"审核结果提交成功","Report");
        }else{
            return new ResponseEntity(200,"审核结果提交失败","certify");
        }
    }

    //获取待审核机构
    @ResponseBody
    @GetMapping("getUnderAuditedCharity")
    public ResponseEntity getUnderAuditedCharity(){
        final List<Charity> underAuditedCharity = adminService.getUnderAuditedCharity();
        if (underAuditedCharity==null){
            return new ResponseEntity(400,"获取未审核机构出错",null);
        }
        String response = JSON.toJSONString(underAuditedCharity, SerializerFeature.PrettyFormat);
        return new ResponseEntity(200,"success",response);
    }

    @ResponseBody
    @GetMapping("getCharityByKey")
    public ResponseEntity getCharityByKey(String charityKey){
//        System.out.println("charityKey------");
//        System.out.println(charityKey);
        final CharityDTO charityDto = charityService.getCharityByKey(charityKey);
        if (charityDto==null){
            return new ResponseEntity(400,"获取机构出错",null);
        }
        String response = JSON.toJSONString(charityDto, SerializerFeature.PrettyFormat);
        return new ResponseEntity(200,"success",response);
    }

    @ResponseBody
    @GetMapping("getCharityById")
    public ResponseEntity getCharityById(Integer charityId){
        final CharityDTO charityDto = charityService.getCharityById(charityId);
        if (charityDto==null){
            return new ResponseEntity(400,"获取机构出错",null);
        }
        String response = JSON.toJSONString(charityDto, SerializerFeature.PrettyFormat);
//        System.out.println(response);
        return new ResponseEntity(200,"success",response);
    }

}
