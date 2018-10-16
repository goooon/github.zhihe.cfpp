package cn.zhihe.ccpp.proj.data.dao.mapper;

import cn.zhihe.ccpp.proj.data.det.Role;
import cn.zhihe.ccpp.proj.data.det.UserRole;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Exrickx
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 通过用户id获取
     * @param userId
     * @return
     */
    List<Role> findByUserId(@Param("userId") String userId);
}
