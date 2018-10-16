package cn.zhihe.ccpp.func.util.log;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LOG {
    private static Object s_logger = null;
    private static boolean  debug = true;  //NOTICE: debug mode will decrease performance.

    static {
        if(s_logger ==null)
            s_logger = log; //获取Native log 实例
    }

    public static void enableDebug(){
        debug = true;
    }

    public static void disableDebug(){
        debug = false;
    }

    public static void D(String msg){
        if(s_logger != null){
            if(debug){
                StackTraceElement traceElement = (new Throwable()).getStackTrace()[1];
                System.out.println("D: "+ msg + " at " + traceElement.getFileName() +":" + traceElement.getLineNumber() + " " + traceElement.getMethodName() +"()");
            }
            else{
                System.out.println("D: " + msg);
            }
        }
    }

    public static void D(String info, List<?> l){
        System.out.print("D: " + info);
        for(Object s : l){
            System.out.print(s.toString() + ";");
        }
        System.out.println("");
    }

    public static void I(String msg){
        if(s_logger != null){
            if(debug){
                StackTraceElement traceElement = (new Throwable()).getStackTrace()[1];
                System.out.println("I: "+ msg + " at " + traceElement.getFileName() +":" + traceElement.getLineNumber() + " " + traceElement.getMethodName() +"()");
            }
            else{
                System.out.println("I: " + msg);
            }
        }
    }

    public static void W(String msg){
        if(s_logger != null){
            if(debug){
                StackTraceElement traceElement = (new Throwable()).getStackTrace()[1];
                System.out.println("W: " + msg + " at " + traceElement.getFileName() +":" + traceElement.getLineNumber() + " " + traceElement.getMethodName() +"()");
            }
            else {
                System.out.println("W: " +msg);
            }
        }
    }

    public static void E(String msg){
        if(s_logger !=null){
            if(debug) {
                StackTraceElement traceElement = (new Throwable()).getStackTrace()[1];
                System.out.println("E: " + msg + " at " + traceElement.getFileName() +":" + traceElement.getLineNumber() + " " + traceElement.getMethodName() +"()");
            }
            else {
                System.out.println("E: " + msg);
            }
        }
    }
}
