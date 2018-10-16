package cn.zhihe.ccpp.proj.data.dao;

import cn.zhihe.ccpp.proj.data.det.Role;
import cn.zhihe.ccpp.core.base.HeDao;

import java.util.List;

/**
 * 角色数据处理层
 * @author Exrickx
 */
public interface RoleDao extends HeDao<Role,String> {

    /**
     * 获取默认角色
     * @param defaultRole
     * @return
     */
    List<Role> findByDefaultRole(Boolean defaultRole);
}
