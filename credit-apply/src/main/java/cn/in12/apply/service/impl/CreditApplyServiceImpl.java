package cn.in12.apply.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.in12.apply.entity.CreditApply;
import cn.in12.apply.mapper.CreditApplyMapper;
import cn.in12.apply.service.CreditApplyService;
import org.springframework.stereotype.Service;

@Service
public class CreditApplyServiceImpl extends ServiceImpl<CreditApplyMapper, CreditApply> implements CreditApplyService {
}
