package cn.zhihe.ccpp.proj.data.dao;

import cn.zhihe.ccpp.core.base.HeDao;
import cn.zhihe.ccpp.proj.data.det.QuartzJob;

import java.util.List;

/**
 * 定时任务数据处理层
 * @author Exrick
 */
public interface QuartzJobDao extends HeDao<QuartzJob,String> {

    /**
     * 通过类名获取
     * @param jobClassName
     * @return
     */
    List<QuartzJob> findByJobClassName(String jobClassName);
}