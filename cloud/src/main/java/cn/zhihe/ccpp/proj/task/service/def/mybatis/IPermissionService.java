package cn.zhihe.ccpp.proj.task.service.def.mybatis;

import cn.zhihe.ccpp.proj.data.det.Permission;
import com.baomidou.mybatisplus.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * @author Exrickx
 */
@CacheConfig(cacheNames = "userPermission")
public interface IPermissionService extends IService<Permission> {

    /**
     * 通过用户id获取
     * @param userId
     * @return
     */
    @Cacheable(key = "#userId")
    List<Permission> findByUserId(String userId);

    /**
     * 通过roleId获取
     * @param roleId
     * @return
     */
    List<Permission> findByRoleId(@Param("roleId") String roleId);
}
