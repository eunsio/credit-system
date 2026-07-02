package cn.in12.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.in12.community.entity.PostReply;
import cn.in12.community.mapper.PostReplyMapper;
import cn.in12.community.service.PostReplyService;
import org.springframework.stereotype.Service;

@Service
public class PostReplyServiceImpl extends ServiceImpl<PostReplyMapper, PostReply> implements PostReplyService {
}
