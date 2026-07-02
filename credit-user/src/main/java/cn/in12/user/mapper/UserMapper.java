package cn.in12.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.in12.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
