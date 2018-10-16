package cn.zhihe.ccpp.proj.task.service.def;


import cn.zhihe.ccpp.core.base.HeService;
import cn.zhihe.ccpp.proj.data.det.UserRole;

import java.util.List;

/**
 * 用户角色接口
 * @author Exrickx
 */
public interface IUserRoleService extends HeService<UserRole,String> {

    /**
     * 通过roleId查找
     * @param roleId
     * @return
     */
    List<UserRole> findByRoleId(String roleId);

    /**
     * 删除用户角色
     * @param userId
     */
    void deleteByUserId(String userId);
}
