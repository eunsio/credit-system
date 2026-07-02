package cn.in12.apply.service.impl;

import cn.in12.apply.mapper.StatMapper;
import cn.in12.apply.service.StatService;
import cn.in12.apply.vo.DashboardVO;
import cn.in12.apply.vo.StatusItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private StatMapper statMapper;

    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();

        Long totalStudents = statMapper.countStudents();
        Long pendingCount = statMapper.countAppliesByStatus(0);
        Long approvedCount = statMapper.countAppliesByStatus(1);
        Long rejectedCount = statMapper.countAppliesByStatus(2);
        Long totalCreditsLong = statMapper.totalApprovedCredits();

        vo.setTotalStudents(totalStudents != null ? totalStudents : 0L);
        vo.setTotalApplies((pendingCount != null ? pendingCount : 0L)
                + (approvedCount != null ? approvedCount : 0L)
                + (rejectedCount != null ? rejectedCount : 0L));
        vo.setPendingCount(pendingCount != null ? pendingCount : 0L);
        vo.setApprovedCount(approvedCount != null ? approvedCount : 0L);
        vo.setRejectedCount(rejectedCount != null ? rejectedCount : 0L);
        vo.setTotalCredits(totalCreditsLong != null ? BigDecimal.valueOf(totalCreditsLong) : BigDecimal.ZERO);

        List<StatusItem> statusDistribution = Arrays.asList(
                new StatusItem("待审核", vo.getPendingCount()),
                new StatusItem("已批准", vo.getApprovedCount()),
                new StatusItem("已驳回", vo.getRejectedCount())
        );
        vo.setStatusDistribution(statusDistribution);

        vo.setYearStats(statMapper.yearStats());
        vo.setGradeStats(statMapper.gradeStats());
        vo.setTopStudents(statMapper.topStudents());

        return vo;
    }
}
