package com.tyut.tmall.util;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * @ClassName PortUtil
 * @Description 帮助检查端口是否启动
 * @Author 王琛
 * @Date 2019/9/25 11:34
 * @Version 1.0
 */
public class PortUtil {
    public static boolean testPort(int port){
        try {
            ServerSocket ss = new ServerSocket(port);
            ss.close();
            return false;
        }catch (java.net.BindException e){
            return true;
        }catch (IOException e) {
            return true;
        }
    }

    public static void checkPort(int port,String server,boolean shutdown){
        if(!testPort(port)){
            if(shutdown){
                String message = String.format("在端口"+port+"未检查到"+server+"启动\n");
                JOptionPane.showMessageDialog(null,message);
                System.exit(1);
            }else{
                String message =String.format("在端口"+port+"未检查到"+server+"启动\n,是否继续");
                if(JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null,message))
                    //退出JVM
                    System.exit(1);
            }
        }
    }
}
