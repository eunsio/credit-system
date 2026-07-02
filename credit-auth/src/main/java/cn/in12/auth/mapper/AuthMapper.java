package cn.in12.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.in12.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper extends BaseMapper<User> {
}
