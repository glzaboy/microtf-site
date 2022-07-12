package com.microtf.framework.utils;

import com.microtf.framework.exceptions.PasswordException;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * 密码生成和验证
 * @author glzaboy
 * @version 1.0
 */
public class PasswordUtil {
    /**
     * 密码生成
     * @param password 密码
     * @return 密码结果
     */
    public static String encodePassword(String password){
        int max=20,min=2,saltLen=5;
        int times = (int) (Math.random()*(max-min)+min);
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuilder saltBuffer=new StringBuilder();
        for(int i=0;i<saltLen;i++){
            int number=random.nextInt(62);
            saltBuffer.append(str.charAt(number));
        }
        String tmpPasswd=password;
        for (int i = 0; i < times; i++) {
            tmpPasswd= DigestUtils.md5DigestAsHex((saltBuffer +tmpPasswd).getBytes(StandardCharsets.UTF_8));
        }
        return String.format("$md5$%d$%s$%s",times, saltBuffer,tmpPasswd);
    }
    /**
     * 验证密码
     * @param passwd 原密码
     * @param encodePasswd 加密后的密码
     * @return 验证结果
     * @throws PasswordException 出错原因
     */
    public static Boolean validPassword(String passwd,String encodePasswd) throws PasswordException {
        String[] passwdSplit = encodePasswd.split("\\$");
        //密码信息因素
        int passwordSplitLen=5;

        if(passwdSplit.length!=passwordSplitLen){
            throw new PasswordException("不支持的加解密方式");
        }
        String alg="md5";
        if(!alg.equalsIgnoreCase(passwdSplit[1])){
            throw new PasswordException("不支持的加解密方式");
        }
        int times=Integer.parseInt(passwdSplit[2]);
        String salt=passwdSplit[3];
        String tmpPasswd=passwd;
        for (int i = 0; i < times; i++) {
            tmpPasswd=DigestUtils.md5DigestAsHex((salt+tmpPasswd).getBytes(StandardCharsets.UTF_8));
        }
        return passwdSplit[4].equalsIgnoreCase(tmpPasswd);
    }
}
