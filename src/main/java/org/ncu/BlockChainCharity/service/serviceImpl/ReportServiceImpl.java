package org.ncu.BlockChainCharity.service.serviceImpl;

import org.ncu.BlockChainCharity.bean.Report;
import org.ncu.BlockChainCharity.mapper.ReportMapper;
import org.ncu.BlockChainCharity.service.ReportService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    Logger logger;

    @Override
    public List<Report> getReportList() {
        return reportMapper.getReportList();
    }

    @Override
    public int addReport(Integer userId, String comment, String projectKey) {
        Report report = new Report();
        report.setUserId(userId);
        report.setComment(comment);
        report.setProjectKey(projectKey);
        //插入举报
        return reportMapper.insertReport(report);
    }

    @Override
    public List<Report> getReportsByUserId(int userId) {
        return reportMapper.getReportsByUserId(userId);
    }

    @Override
    public Report getReportById(int reportId) {
        return reportMapper.selectReportById(reportId);
    }

    @Override
    public List<Report> getReportsUnsolved() {
        return reportMapper.getReportUnsolved();
    }

    @Override
    public int solveReport(int reportId, String result) {
        return reportMapper.updateReport(reportId,result);
    }

}
