package cn.zhihe.ccpp.proj.task.service.def;

import cn.zhihe.ccpp.core.base.HeService;
import cn.zhihe.ccpp.proj.data.det.QuartzJob;

import java.util.List;

/**
 * 定时任务接口
 * @author Exrick
 */
public interface IQuartzJobService extends HeService<QuartzJob,String> {

    /**
     * 通过类名获取
     * @param jobClassName
     * @return
     */
    List<QuartzJob> findByJobClassName(String jobClassName);
}