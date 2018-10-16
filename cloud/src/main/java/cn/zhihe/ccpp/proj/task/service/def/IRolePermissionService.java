package cn.zhihe.ccpp.proj.task.service.def;

import cn.zhihe.ccpp.core.base.HeService;
import cn.zhihe.ccpp.proj.data.det.RolePermission;

import java.util.List;

/**
 * 角色权限接口
 * @author Exrick
 */
public interface IRolePermissionService extends HeService<RolePermission,String> {

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