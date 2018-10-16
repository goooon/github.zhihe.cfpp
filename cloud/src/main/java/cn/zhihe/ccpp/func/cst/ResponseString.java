package cn.zhihe.ccpp.func.cst;

public interface ResponseString {
    String USERNAME_OR_PASSWORD_ERROR = "用户名或密码错误";
    String ACCOUNT_FORBIDDEN = "账户被禁用，请联系管理员";
    String OTHER_ONTERNAL_ERROR = "登录失败，其他内部错误";
    String MAX_USERNAME_OR_PASSWORD = "用户名或密码错误，还有%d次尝试机会";
    String MAX_ERROR_REACHED = "登录错误次数超过限制，请%d分钟后再试";
    String LOGIN_SUCCESS = "登录成功";
}
