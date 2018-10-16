package cn.zhihe.ccpp.proj.task.service.imp;

import cn.zhihe.ccpp.proj.data.det.Permission;
import cn.zhihe.ccpp.proj.data.dao.PermissionDao;
import cn.zhihe.ccpp.proj.task.service.def.IPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 权限接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public PermissionDao getRepository() {
        return permissionDao;
    }

    @Override
    public List<Permission> findByLevelOrderBySortOrder(Integer level) {

        return permissionDao.findByLevelOrderBySortOrder(level);
    }

    @Override
    public List<Permission> findByParentIdOrderBySortOrder(String parentId) {

        return permissionDao.findByParentIdOrderBySortOrder(parentId);
    }

    @Override
    public List<Permission> findByTypeAndStatusOrderBySortOrder(Integer type, Integer status) {

        return permissionDao.findByTypeAndStatusOrderBySortOrder(type, status);
    }

    @Override
    public List<Permission> findByTitle(String title) {

        return permissionDao.findByTitle(title);
    }
}