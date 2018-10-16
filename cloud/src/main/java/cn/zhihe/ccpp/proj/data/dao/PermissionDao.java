package cn.zhihe.ccpp.proj.data.dao;

import cn.zhihe.ccpp.proj.data.det.Permission;
import cn.zhihe.ccpp.core.base.HeDao;

import java.util.List;

/**
 * 权限数据处理层
 * @author Exrick
 */
public interface PermissionDao extends HeDao<Permission,String> {

    /**
     * 通过层级查找
     * 默认升序
     * @param level
     * @return
     */
    List<Permission> findByLevelOrderBySortOrder(Integer level);

    /**
     * 通过parendId查找
     * @param parentId
     * @return
     */
    List<Permission> findByParentIdOrderBySortOrder(String parentId);

    /**
     * 通过类型和状态获取
     * @param type
     * @param status
     * @return
     */
    List<Permission> findByTypeAndStatusOrderBySortOrder(Integer type, Integer status);

    /**
     * 通过名称获取
     * @param title
     * @return
     */
    List<Permission> findByTitle(String title);
}