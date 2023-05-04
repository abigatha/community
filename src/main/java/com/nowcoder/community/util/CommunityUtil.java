package com.nowcoder.community.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密,无法解密
    //hello -> abc123def456
    //hello + 3e4a8(salt) -> abd123def456abc
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;//空密码不加密
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());//加密成16进制字符串
    }
    //封装json工具
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(map != null){
            for(String key : map.keySet()){
                jsonObject.put(key,map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    //重载json工具
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    //重载json工具
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

}
