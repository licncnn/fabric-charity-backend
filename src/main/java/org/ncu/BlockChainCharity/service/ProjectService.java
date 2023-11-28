package org.ncu.BlockChainCharity.service;



import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.dto.ProjectDTO;

import java.io.IOException;
import java.util.ArrayList;

public interface ProjectService {

    public ProjectDTO insertProject(String projectName, String comment, double target,
                                       String charityKey, Long endTime, String  materialPath);

    public BlockchainDTO addProject(int projectId) throws IOException;

    public BlockchainDTO getAllProjects() throws Exception;

    public BlockchainDTO getProjectByKey(String projectKey) throws  Exception;

    public BlockchainDTO getProjectsByCharityKey(String charityKey) throws IOException;

    public ArrayList getProjectsNotAudited();

    BlockchainDTO updateProjectStatus(String projectKey, String newStatus) throws IOException;


}
