package cn.zhihe.ccpp.proj.task.service.imp.mybatis;

import cn.zhihe.ccpp.proj.data.dao.mapper.UserRoleMapper;
import cn.zhihe.ccpp.proj.data.det.Role;
import cn.zhihe.ccpp.proj.data.det.UserRole;
import cn.zhihe.ccpp.proj.task.service.def.mybatis.IUserRoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Exrickx
 */
@Service
public class IUserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Role> findByUserId(String userId) {

        return userRoleMapper.findByUserId(userId);
    }
}
