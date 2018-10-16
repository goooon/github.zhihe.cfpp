package cn.zhihe.ccpp.proc.data.amc.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BigThingAddRequest {

    @ApiModelProperty("数据ID")
    private Long Id;

    @ApiModelProperty("大事件内容")
    private String content;

    @ApiModelProperty("大事件名称")
    private String title;

    @ApiModelProperty("学期id")
    private Long termId;

    @ApiModelProperty("创建人id")
    private Long personnelId;

    @ApiModelProperty("幼儿园id")
    private Long kindergartenId;

}
