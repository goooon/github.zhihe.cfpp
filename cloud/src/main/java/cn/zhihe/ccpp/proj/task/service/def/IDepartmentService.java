package cn.zhihe.ccpp.proj.task.service.def;

import cn.zhihe.ccpp.proj.data.det.Department;
import cn.zhihe.ccpp.core.base.HeService;

import java.util.List;

/**
 * 部门接口
 * @author Exrick
 */
public interface IDepartmentService extends HeService<Department,String> {

    /**
     * 通过父id获取 升序
     * @param parentId
     * @return
     */
    List<Department> findByParentIdOrderBySortOrder(String parentId);

    /**
     * 通过父id和状态获取
     * @param parentId
     * @param status
     * @return
     */
    List<Department> findByParentIdAndStatusOrderBySortOrder(String parentId, Integer status);
}