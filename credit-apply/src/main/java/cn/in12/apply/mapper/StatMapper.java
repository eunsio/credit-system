package cn.in12.apply.mapper;

import cn.in12.apply.vo.GradeStatItem;
import cn.in12.apply.vo.TopStudentItem;
import cn.in12.apply.vo.YearItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatMapper {

    Long countStudents();

    Long countAppliesByStatus(Integer status);

    Long totalApprovedCredits();

    List<YearItem> yearStats();

    List<TopStudentItem> topStudents();

    List<GradeStatItem> gradeStats();
}
