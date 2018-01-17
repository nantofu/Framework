package cn.xuhao.android.lib.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 *
 * @author xuhao
 */
public class Reflex {

    private Reflex() {
    }

    /**
     * 通过反射修改（包括非public的）属性。在寻找该属性时，会一直递归到（但不包括）Object class。
     *
     * @param obj 调用该对象所在类的属性
     * @param fieldName 属性名
     * @param newValue 修改成该值
     * @return 如果成功找到并修改该属性，返回true，否则返回false
     */
    public static boolean modifyField(@Nullable Object obj, String fieldName, Object newValue) {
        if (obj == null) {
            return false;
        }

        boolean retval = false;

        Field field = getField(obj, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(obj, newValue);
                retval = true;
            } catch (IllegalAccessException iae) {
                throw new IllegalArgumentException(fieldName, iae);
            } catch (ExceptionInInitializerError eiie) {
                throw new IllegalArgumentException(fieldName, eiie);
            }
        }

        return retval;
    }

    /**
     * 递归查找声明的属性，一直递归到（但不包括）Object class。
     *
     * @param object 指定对象
     * @param fieldName 属性名
     * @return 属性
     */
    public static Field getField(@NonNull Object object, String fieldName) {
        Field field;

        Class<?> theClass = object.getClass();
        for (; theClass != Object.class; theClass = theClass.getSuperclass()) {
            try {
                field = theClass.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {  //SUPPRESS CHECKSTYLE

            } catch (SecurityException e) {
                throw new IllegalArgumentException(theClass.getName() + "." + theClass, e);
            }
        }

        return null;
    }

    /**
     * 调用一个对象的隐藏方法。
     *
     * @param obj 调用方法的对象.
     * @param methodName 方法名。
     * @param types 方法的参数类型。
     * @param args 方法的参数。
     * @return 如果调用成功，则返回true。
     */
    public static boolean invokeHideMethod(@NonNull Object obj, String methodName, Class<?>[] types, Object[] args) {
        boolean hasInvoked = false;
        try {
            Method method = obj.getClass().getMethod(methodName, types);
            method.invoke(obj, args);
            hasInvoked = true;
        } catch (Exception ignore) {    // SUPPRESS CHECKSTYLE
        }
        return hasInvoked;
    }

    /**
     * 调用一个对象的隐藏方法。
     *
     * @param obj 调用方法的对象.
     * @param methodName 方法名。
     * @param types 方法的参数类型。
     * @param args 方法的参数。
     * @param result 如果该方法有返回值，则将返回值放入result[0]中；否则将result[0]置为null
     * @return 隐藏方法调用的返回值。
     */
    public static boolean invokeHideMethod(@NonNull Object obj,
            String methodName, Class<?>[] types, Object[] args, @Nullable Object[] result) {
        boolean retval = false;
        try {
            Method method = obj.getClass().getMethod(methodName, types);
            final Object invocationResult = method.invoke(obj, args);
            if (result != null && result.length > 0) {
                result[0] = invocationResult;
            }
            retval = true;
        } catch (Exception ignore) {    // SUPPRESS CHECKSTYLE
        }
        return retval;
    }

    /**
     * 反射调用（包括非public的）方法。在寻找该方法时，会一直递归到（但不包括）Object class。
     *
     * @param obj 调用该对象所在类的非public方法
     * @param methodName 方法名
     * @param result 如果该方法有返回值，则将返回值放入result[0]中；否则将result[0]置为null
     * @return 如果成功找到并调用该方法，返回true，否则返回false
     */
    public static boolean invokeMethod(Object obj, String methodName, Object[] result) {
        return invokeMethod(obj, methodName, null, result);
    }

    /**
     * 反射调用（包括非public的）方法。在寻找该方法时，会一直递归到（但不包括）Object class。
     *
     * @param obj 调用该对象所在类的非public方法
     * @param methodName 方法名
     * @param params 调用该方法需要的参数
     * @param result 如果该方法有返回值，则将返回值放入result[0]中；否则将result[0]置为null
     * @return 如果成功找到并调用该方法，返回true，否则返回false
     */
    public static boolean invokeMethod(Object obj, String methodName, @Nullable Object[] params,
            Object[] result) { // SUPPRESS CHECKSTYLE
        Class<?>[] paramTypes = params == null ? null : new Class<?>[params.length];
        if (params != null) {
            for (int i = 0; i < params.length; ++i) {
                paramTypes[i] = params[i] == null ? null : params[i].getClass();
            }
        }
        return invokeMethod(obj, methodName, paramTypes, params, result);
    }

    /**
     * 反射调用（包括非public的）方法。在寻找该方法时，会一直递归到（但不包括）Object class。
     *
     * @param obj 调用该对象所在类的非public方法
     * @param methodName 方法名
     * @param paramTypes 该方法所有参数的类型
     * @param params 调用该方法需要的参数
     * @param result 如果该方法有返回值，则将返回值放入result[0]中；否则将result[0]置为null
     * @return 如果成功找到并调用该方法，返回true，否则返回false
     */
    public static boolean invokeMethod(@Nullable Object obj, String methodName, Class<?>[] paramTypes, Object[] params,
            // SUPPRESS CHECKSTYLE
            @Nullable Object[] result) { // SUPPRESS CHECKSTYLE
        if (obj == null) {
            return false;
        }

        boolean retval = false;

        Method method = getMethod(obj, methodName, paramTypes);
        // invoke
        if (method != null) {
            method.setAccessible(true);
            try {
                final Object invocationResult = method.invoke(obj, params);
                if (result != null && result.length > 0) {
                    result[0] = invocationResult;
                }
                retval = true;
            } catch (IllegalAccessException iae) {
                throw new IllegalArgumentException(methodName, iae);
            } catch (InvocationTargetException ite) {
                throw new IllegalArgumentException(methodName, ite);
            } catch (ExceptionInInitializerError eiie) {
                throw new IllegalArgumentException(methodName, eiie);
            }
        }

        return retval;
    }

    /**
     * 递归查找声明的方法，一直递归到（但不包括）Object class。
     *
     * @param object 指定对象
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 指定对象的方法
     */
    public static Method getMethod(@NonNull Object object, String methodName, Class<?>[] paramTypes) {
        Method method = null;

        Class<?> theClass = object.getClass();
        for (; theClass != Object.class; theClass = theClass.getSuperclass()) {
            try {
                method = theClass.getDeclaredMethod(methodName, paramTypes);
                return method;
            } catch (NoSuchMethodException e) {  //SUPPRESS CHECKSTYLE

            } catch (SecurityException e) {
                throw new IllegalArgumentException(theClass.getName() + "." + methodName, e);
            }
        }

        return null;
    }

}
