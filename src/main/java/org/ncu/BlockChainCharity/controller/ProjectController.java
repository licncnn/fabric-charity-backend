package org.ncu.BlockChainCharity.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.ncu.BlockChainCharity.bean.Project;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.ProjectDTO;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.ncu.BlockChainCharity.reqDto.UploadProjectDto;
import org.ncu.BlockChainCharity.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;


@Controller
@RequestMapping("project")
public class ProjectController {


    @Autowired
    ProjectService projectService;

    @ResponseBody
    @GetMapping("getAllProjects")
    public ResponseEntity queryAllProjects() throws Exception{

        BlockchainDTO blockchainDTO = projectService.getAllProjects();
        String error = blockchainDTO.getError();
        if(error!=null){
           return new ResponseEntity(400,error,null);
        }else{
            return new ResponseEntity(200,"success",blockchainDTO.getResult());
        }
    }


    @ResponseBody
    @GetMapping("getProject")
    public ResponseEntity queryProject(String projectKey) throws Exception{
        BlockchainDTO blockchainDTO = projectService.getProjectByKey(projectKey);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


    @ResponseBody
    @GetMapping("getProjectsByCharityKey")
    public ResponseEntity getProjectsByCharityKey(String charityKey) throws IOException {

        BlockchainDTO blockchainDTO = projectService.getProjectsByCharityKey(charityKey);
        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }

    @ResponseBody
    @GetMapping("addProject")
    public ResponseEntity addProject(int projectId) throws IOException {
        BlockchainDTO blockchainDTO = projectService.addProject(projectId);

        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }

    @ResponseBody
    @PostMapping("uploadProject")
    public ResponseEntity uploadProject(@RequestBody UploadProjectDto uploadProjectDto, HttpSession httpSession){

        //TODO 把charityId 设置回来
//        Charity charity = (Charity) httpSession.getAttribute("charity");
//        if(charity.getIsAudited()==0){
//            return new ResponseEntity(200,"未认证",null);
//        }

        ProjectDTO projectDTO;
        projectDTO = projectService.insertProject(uploadProjectDto.getProjectName(),uploadProjectDto.getComment(),uploadProjectDto.getTarget(),"CHARITY000",uploadProjectDto.getEndTime(),uploadProjectDto.getMaterialPath());
//        projectDTO = projectService.insertProject(projectName,comment,target,charity.getCharityId(),endTime,material);
        String error = projectDTO.getError();
        if(error!=null) {
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"上传项目success",projectDTO.getProject());
    }




    @ResponseBody
    @GetMapping("getProjectsNotAudited")
    public ResponseEntity getProjectsNotAudited(){
        final ArrayList<Project> projectsNotAudited = projectService.getProjectsNotAudited();
        if (projectsNotAudited==null){
            return new ResponseEntity(400,"获取未审核项目出错",null);
        }else{
            return new ResponseEntity(200,"success", JSON.toJSONString(projectsNotAudited, SerializerFeature.PrettyFormat));
        }
    }


    @ResponseBody
    @PostMapping("changeProjectStatus")
    public ResponseEntity changeProjectStatus(String projectKey, String newStatus) throws IOException {
        BlockchainDTO blockchainDTO = projectService.updateProjectStatus(projectKey,newStatus);

        String error = blockchainDTO.getError();
        if(error!=null){
            return new ResponseEntity(400,error,null);
        }
        return new ResponseEntity(200,"success",blockchainDTO.getResult());
    }


}
