package cn.in12.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.in12.community.entity.CommunityPost;
import cn.in12.community.mapper.CommunityPostMapper;
import cn.in12.community.service.CommunityPostService;
import org.springframework.stereotype.Service;

@Service
public class CommunityPostServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost> implements CommunityPostService {
}
