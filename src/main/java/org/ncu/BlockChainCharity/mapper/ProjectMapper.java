package org.ncu.BlockChainCharity.mapper;

import org.ncu.BlockChainCharity.bean.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;


@Mapper
public interface ProjectMapper  {
    //插入项目到数据库
    Integer insertProject(Project project);
    //更新项目的key
    Integer updateProjectKey(@Param("projectId")Integer projectId,@Param("projectKey")String projectKey);
    //过去所有为审核的项目
    ArrayList<Project> getProjectsNotAudited();
    //更新项目状态
    Integer updateProjectStatus(@Param("projectKey")String projectKey,@Param("newStatus")String newStatus);
    //更新项目当前金额
    Integer updateProjectCurrent(@Param("projectKey")String projectKey,@Param("amount")double amount);
    //更新项目总支出
    Integer updateProjectTotalExpense(@Param("projectKey")String projectKey,@Param("amount")double amount);

    Project selectProjectById(int projectId);

    String getCharityKeyByProjectKey(String projectKey);
}
