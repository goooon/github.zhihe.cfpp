package cn.zhihe.ccpp.proc.data.amc.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BigThingListResponse {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("大事件名称")
    private String title;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("创建人")
    private String teacherName;

}
