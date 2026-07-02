package cn.in12.competition.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.in12.competition.entity.Competition;
import cn.in12.competition.mapper.CompetitionMapper;
import cn.in12.competition.service.CompetitionService;
import org.springframework.stereotype.Service;

@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {
}
