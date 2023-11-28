package org.ncu.BlockChainCharity.service;



import org.ncu.BlockChainCharity.bean.Report;

import java.util.List;

public interface ReportService {
    List<Report> getReportList ();
    int addReport(Integer userId,String comment,String projectKey);
    List<Report> getReportsByUserId(int userId);
    Report getReportById(int reportId);
    List<Report> getReportsUnsolved();
    int solveReport(int reportId,String report);
}
