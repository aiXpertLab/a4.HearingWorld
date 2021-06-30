package com.seeingvoice.www.svhearing.database;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Arrays;
import java.util.List;

/**
 * 把测试结果字符串的集合转换成字符串存入到数据库中
 * Date:2019/3/14
 * Time:10:46
 * auther:zyy
 */
public class StringConverter implements PropertyConverter<List<String>,String> {


    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null){
            return null;
        }else {
            List<String> list = Arrays.asList(databaseValue.split(","));
            return list;
        }
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if (entityProperty == null){
            return null;
        }else {
            StringBuilder sb = new StringBuilder();
            for (String s:entityProperty){
                sb.append(s);
                sb.append(",");
            }
            return sb.toString();
        }
    }
}
