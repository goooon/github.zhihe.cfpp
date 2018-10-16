package cn.zhihe.ccpp.proj.task.service.imp;

import cn.zhihe.ccpp.proj.data.dao.RolePermissionDao;
import cn.zhihe.ccpp.proj.data.det.RolePermission;
import cn.zhihe.ccpp.proj.task.service.def.IRolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色权限接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class RolePermissionServiceImpl implements IRolePermissionService {

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Override
    public RolePermissionDao getRepository() {
        return rolePermissionDao;
    }

    @Override
    public List<RolePermission> findByPermissionId(String permissionId) {

        return rolePermissionDao.findByPermissionId(permissionId);
    }

    @Override
    public void deleteByRoleId(String roleId) {

        rolePermissionDao.deleteByRoleId(roleId);
    }
}