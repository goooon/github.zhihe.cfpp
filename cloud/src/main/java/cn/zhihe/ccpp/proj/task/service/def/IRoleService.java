package cn.zhihe.ccpp.proj.task.service.def;


import cn.zhihe.ccpp.core.base.HeService;
import cn.zhihe.ccpp.proj.data.det.Role;

import java.util.List;

/**
 * 角色接口
 * @author Exrickx
 */
public interface IRoleService extends HeService<Role,String> {

    /**
     * 获取默认角色
     * @param defaultRole
     * @return
     */
    List<Role> findByDefaultRole(Boolean defaultRole);
}
