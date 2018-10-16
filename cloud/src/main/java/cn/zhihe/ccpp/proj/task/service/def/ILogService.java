package cn.zhihe.ccpp.proj.task.service.def;


import cn.zhihe.ccpp.proj.data.det.Log;
import cn.zhihe.ccpp.core.base.HeService;
import cn.zhihe.ccpp.proj.tool.vo.SearchVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 日志接口
 * @author Exrickx
 */
public interface ILogService extends HeService<Log,String> {

    /**
     * 日志搜索
     * @param key
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<Log> searchLog(String key, SearchVo searchVo, Pageable pageable);

    /**
     * 删除所有
     */
    void deleteAll();
}
