package org.ncu.BlockChainCharity.service.serviceImpl;

import lombok.AllArgsConstructor;
import org.ncu.BlockChainCharity.bean.Project;
import org.ncu.BlockChainCharity.common.ContractErrorMessage;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.ProjectDTO;
import org.ncu.BlockChainCharity.mapper.ProjectMapper;
import org.ncu.BlockChainCharity.service.ProjectService;

import org.hyperledger.fabric.client.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;


@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    static final String MATERIALPATH="/Users/dubai/fabric/hyperledger-fabric-app-java-demo/src/main/resources/static/upload/material";

    @Autowired
    Logger logger;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    final Gateway gateway;

    @Autowired
    final Contract contract;




    @Override
    public ProjectDTO insertProject(String projectName, String comment, double target, String charityKey, Long endTime, String  materialPath) {
        ProjectDTO projectDTO = new ProjectDTO();
        Project project = new Project();
        project.setProjectName(projectName);
        project.setCharityKey(charityKey);
        project.setComment(comment);
        project.setTarget(target);


        project.setCurrent(0);
        //为审核的项目，status为0，审核过的为1
        project.setStatus("0");

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        project.setCreateTime(currentTimestamp);

        //TODO  修复结束时间的BUG
        project.setEndTime(new Timestamp(endTime));
//        String fileName = UUID.randomUUID().toString().replace("-","")+material.getOriginalFilename();
//        File targetFile =new File(MATERIALPATH,fileName);
//        if(!targetFile.getParentFile().exists()){
//            targetFile.getParentFile().mkdirs();
//        }
//        try{
//            material.transferTo(targetFile);
//        } catch (IOException ioException) {
//            projectDTO.setError("上传错误");
//            logger.error("上传项目材料出错");
//            return projectDTO;
//        }
//        String filePath = MATERIALPATH+"/"+fileName;
        project.setMaterialPath(materialPath);

        //TODO 感觉这里可以加事务
        if(projectMapper.insertProject(project)==0){
            logger.error("用户上传项目失败");
            projectDTO.setError("上传出错");
            return projectDTO;
        }
        String projectKey = String.format("PROJECT%03d",project.getProjectId());

        project.setProjectKey(projectKey);
        projectDTO.setProject(project);

//        System.out.println("===>"+projectKey);    //PROJECT002
//        System.out.println(project.getProjectId()); // 2

//        projectMapper.updateProjectKey(project.getProjectId(),"PROJECT002");

        if(projectMapper.updateProjectKey(project.getProjectId(),projectKey)==0){
            logger.error("更新项目键值失败");
            projectDTO.setError("上传出错");
        }
        return projectDTO;
    }



    @Override
    public BlockchainDTO addProject(int projectId) throws IOException {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        Project project = projectMapper.selectProjectById(projectId);
        System.out.println(project);
        byte[] result;

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        try {
            result = contract.submitTransaction("createProject", project.getProjectKey(), project.getProjectName(), project.getCharityKey() , project.getComment(),
                    project.getTarget() + "", currentTime.toString(), project.getMaterialPath(), project.getEndTime() + "", "1");

            System.out.println(new String(result));
            blockchainDTO.setResult(new String(result));
        } catch (EndorseException endorseException) {
            endorseException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.EndorseException.getDis());
            return blockchainDTO;
        } catch (CommitException commitException) {
            commitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.CommitException.getDis());
            return blockchainDTO;
        } catch (SubmitException submitException) {
            submitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.SubmitException.getDis());
            return blockchainDTO;
        } catch (CommitStatusException commitStatusException) {
            commitStatusException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.CONTRACT.getDis());
            return blockchainDTO;
        }


        long startTime = System.currentTimeMillis();
        if(projectMapper.updateProjectStatus(project.getProjectKey(),"1")==0){
            logger.error("数据库更新项目状态异常，");
            blockchainDTO.setError("数据库同步数据失败，可能造成数据不一致");
        }
        return blockchainDTO;
    }


    public BlockchainDTO getAllProjects() throws IOException {

        BlockchainDTO blockchainDTO = new BlockchainDTO();

//        Contract contract = BlockGateway.getContract();
        byte[] result;
        try{
            result = contract.evaluateTransaction("queryAllProjects");
            blockchainDTO.setResult(new String(result));
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
            blockchainDTO.setError(ContractErrorMessage.CommitStatusException.getDis());
            return blockchainDTO;
        } catch (GatewayException contractException){
            blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
            contractException.printStackTrace();
        }
        return blockchainDTO;
    }


    @Override
    public BlockchainDTO getProjectByKey(String projectKey) throws IOException {

        BlockchainDTO blockchainDTO = new BlockchainDTO();

//        Contract contract = BlockGateway.getContract();
        byte[] result;
        try{
            result = contract.evaluateTransaction("queryProject",projectKey);
            blockchainDTO.setResult(new String(result));


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
            blockchainDTO.setError(ContractErrorMessage.CommitStatusException.getDis());
            return blockchainDTO;
        } catch (GatewayException contractException){
            blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
            contractException.printStackTrace();
        } catch (Exception e){
            blockchainDTO.setError("找不到对应项目");
            e.printStackTrace();
        }
        return blockchainDTO;
    }


    @Override
    public BlockchainDTO getProjectsByCharityKey(String charityKey) throws IOException {
        BlockchainDTO blockchainDTO = new BlockchainDTO();
        try{
            byte[] result = contract.evaluateTransaction("queryProjectsByCharityKey",charityKey);
            blockchainDTO.setResult(new String(result));
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
            blockchainDTO.setError(ContractErrorMessage.CommitStatusException.getDis());
            return blockchainDTO;
        } catch (GatewayException contractException){
            blockchainDTO.setError(ContractErrorMessage.GatewayException.getDis());
            contractException.printStackTrace();
        }
        return blockchainDTO;
    }



    @Override
    public ArrayList<Project> getProjectsNotAudited() {
        ArrayList<Project> projects = projectMapper.getProjectsNotAudited();

        if(projects==null){
            logger.warn("查询未审核项目失败");
        }
        return projects;
    }


    // 使用这个方法把 status 的状态由1改成2
    @Override
    public BlockchainDTO updateProjectStatus(String projectKey, String newStatus) throws IOException {
        BlockchainDTO blockchainDTO = new BlockchainDTO();

        // 修改数据库中的字段
        projectMapper.updateProjectStatus(projectKey,newStatus);
        System.out.println("数据库状态修改成功");
//        Contract contract = BlockGateway.getContract();
        try {
            // TODO 这个方法执行失败了
            byte[] result = contract.submitTransaction("updateProjectStatus", projectKey, newStatus);
            System.out.println(new String(result));
            blockchainDTO.setResult(new String(result));

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
            blockchainDTO.setError(ContractErrorMessage.CommitStatusException.getDis());
            return blockchainDTO;
        } catch (CommitException commitException) {
            commitException.printStackTrace();
            blockchainDTO.setError(ContractErrorMessage.CommitException.getDis());
            return blockchainDTO;
        }
        return blockchainDTO;
    }

}
