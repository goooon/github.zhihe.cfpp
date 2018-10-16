package cn.zhihe.ccpp.func.util.exception;

import lombok.Data;

/**
 * @author Exrickx
 */
@Data
public class CfppException extends RuntimeException {

    private String msg;

    public CfppException(String msg){
        super(msg);
        this.msg = msg;
    }
}
