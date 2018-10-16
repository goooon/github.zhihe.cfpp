package cn.zhihe.ccpp.proj.data.dao;

import cn.zhihe.ccpp.proj.data.det.RolePermission;
import cn.zhihe.ccpp.core.base.HeDao;

import java.util.List;

/**
 * 角色权限数据处理层
 * @author Exrick
 */
public interface RolePermissionDao extends HeDao<RolePermission,String> {

    /**
     * 通过permissionId获取
     * @param permissionId
     * @return
     */
    List<RolePermission> findByPermissionId(String permissionId);

    /**
     * 通过roleId删除
     * @param roleId
     */
    void deleteByRoleId(String roleId);
}