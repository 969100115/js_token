package com.weixin.sdk.common.exception.asserts;


import com.weixin.sdk.common.exception.BaseException;
import com.weixin.sdk.http.response.ResultEnum;

/**
 * 统一异常抛出基类
 * @author Wenbo
 * @date 2021/1/8 11:38
 * @Email 969100115@qq.com
 * @phone 17621847037
 */
public abstract class Assert {


    public class AssertResult<T extends Object>{
        Boolean isAssert;
        T object;

        public AssertResult(T object) {
            this.object = object;
        }

        public AssertResult(Boolean isAssert, T object) {
            this.isAssert = isAssert;
            this.object = object;
        }


        /**
         * todo 实现中T
         * @param resultEnum
         * @param description
         */
        public T doAssert(ResultEnum resultEnum, String description){
            if(this.isAssert){
                throwException(resultEnum,description);
            }
            return this.getObject();
        }

        public Boolean getAssert() {
            return isAssert;
        }

        public void setAssert(Boolean anAssert) {
            isAssert = anAssert;
        }

        public T getObject() {
            return object;
        }

        public void setObject(T object) {
            this.object = object;
        }
    }

    /**
     * 由实现类定义抛出异常的类型
     *
     * @param resultEnum 异常返回枚举，用于定义返回code与message
     * @param description 异常描述，用于异常日志打印
     * @return
     */
    abstract BaseException throwException(ResultEnum resultEnum, String description);

    /**
     * 默认异常抛出，抛出BaseException
     * @param resultEnum 异常返回枚举，用于定义返回code与message
     * @param description 异常描述，用于异常日志打印
     */
    public static void defaultAssert(ResultEnum resultEnum,String description){
        throw new BaseException(resultEnum,description);
    }

    /**
     * 默认异常抛出，抛出BaseException
     * @param resultEnum 异常返回枚举，用于定义返回code与message
     */
    public static void defaultAssert(ResultEnum resultEnum){
        throw new BaseException(resultEnum);
    }


}
