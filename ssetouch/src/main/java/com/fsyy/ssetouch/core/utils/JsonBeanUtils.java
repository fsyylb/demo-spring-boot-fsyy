package com.fsyy.ssetouch.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * JsonBean转化工具
 */
public class JsonBeanUtils {
    private static ObjectMapper  objectMapper = new ObjectMapper();

    private JsonBeanUtils(){
        super();
    }

    /**
     * bean转json字符串
     * 低版本jackson无toPrettyString()方法
     *
     * @param bean
     * @return
     * @throws IOException
     */
    public static String beanToJson(Object bean) throws IOException {
        String jsonText = objectMapper.writeValueAsString(bean);
        return objectMapper.readTree(jsonText).toPrettyString();
    }

    /**
     * bean转json字符串
     *
     * @param bean
     * @param pretty 是否格式美化
     * @return
     * @throws IOException
     */
    public static String beanToJson(Object bean, boolean pretty) throws IOException {
        if(pretty){
            return beanToJson(bean);
        }
        String jsonText = objectMapper.writeValueAsString(bean);
        return objectMapper.readTree(jsonText).toString();
    }

    /**
     * json字符串转bean
     *
     * @param jsonText
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonToBean(String jsonText, Class<T> clazz) throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return objectMapper.readValue(jsonText, clazz);
    }

    /**
     * json字符串转bean
     *
     * @param jsonText
     * @param clazz
     * @param ignoreError 是否忽略无法识别字段
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonToBean(String jsonText, Class<T> clazz, boolean ignoreError) throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !ignoreError);
        return objectMapper.readValue(jsonText, clazz);
    }

    /**
     * json字符串转bean
     *
     * @param jsonText
     * @param javaType
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonToBean(String jsonText, JavaType javaType) throws IOException {
        return objectMapper.readValue(jsonText, javaType);
    }

    /**
     * json字符串转bean
     *
     * @param jsonText
     * @param typeRef
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonToBean(String jsonText, TypeReference<T> typeRef) throws IOException {
        return objectMapper.readValue(jsonText, typeRef);
    }
}
