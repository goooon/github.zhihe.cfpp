package cn.zhihe.ccpp.proj.data.dao;

import cn.zhihe.ccpp.core.base.HeDao;
import cn.zhihe.ccpp.proj.data.det.UserRole;

import java.util.List;

/**
 * 用户角色数据处理层
 * @author Exrickx
 */
public interface UserRoleDao extends HeDao<UserRole,String> {

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
