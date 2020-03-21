package sorm.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtils {
    /**
     * 根据属性名称 获得属性的value值  方法：反射得到属性的get方法然后调用
     * @param FieldName   属性名
     * @param o        实例对象
     * @return   属性对应的value值
     */
    public static Object invokeGet(String FieldName,Object o){
        Class<?> clazz = o.getClass();
        try {
            Method getPriKey = clazz.getDeclaredMethod("get" + StringUtils.firstChar2Uppercase(FieldName));
            return getPriKey.invoke(o);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * 通过反射调用Javabean的set方法设置值 这个方法只支持反射得到的函数只有一个参数
     * @param FieldName  属性名
     * @param o    Javabean对象
     * @param FieldValue  设置进去的属性值
     */
    public static void invokeSet(Object o,String FieldName,Object FieldValue){
        Class<?> clazz = o.getClass();
        try {
            Method m = clazz.getDeclaredMethod("set" + StringUtils.firstChar2Uppercase(FieldName), FieldValue.getClass());
            m.invoke(o,FieldValue);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
