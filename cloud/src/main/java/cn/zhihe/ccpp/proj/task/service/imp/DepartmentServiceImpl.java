package cn.zhihe.ccpp.proj.task.service.imp;

import cn.zhihe.ccpp.proj.data.dao.DepartmentDao;
import cn.zhihe.ccpp.proj.data.det.Department;
import cn.zhihe.ccpp.proj.task.service.def.IDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    public DepartmentDao getRepository() {
        return departmentDao;
    }

    @Override
    public List<Department> findByParentIdOrderBySortOrder(String parentId) {

        return departmentDao.findByParentIdOrderBySortOrder(parentId);
    }

    @Override
    public List<Department> findByParentIdAndStatusOrderBySortOrder(String parentId, Integer status) {

        return departmentDao.findByParentIdAndStatusOrderBySortOrder(parentId, status);
    }
}