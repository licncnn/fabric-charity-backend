package org.ncu.BlockChainCharity.dto;


import org.ncu.BlockChainCharity.bean.Project;
import lombok.Data;

@Data
public class ProjectDTO {
    Project project;
    String error;
    String result;
}
