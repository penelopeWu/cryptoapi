package com.example.cryptoapi.springmvc.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DecryptEncryptFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter implements InitializingBean {

    private RequestDecryptResponseEncryptBodyProcessor requestDecryptResponseEncryptBodyProcessor;

    public RequestDecryptResponseEncryptBodyProcessor getRequestDecryptResponseEncryptBodyProcessor() {
        return requestDecryptResponseEncryptBodyProcessor;
    }

    public void setRequestDecryptResponseEncryptBodyProcessor(RequestDecryptResponseEncryptBodyProcessor requestDecryptResponseEncryptBodyProcessor) {
        this.requestDecryptResponseEncryptBodyProcessor = requestDecryptResponseEncryptBodyProcessor;
    }

    public DecryptEncryptFastJsonHttpMessageConverter() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        SerializerFeature[] serializerFeatures = new SerializerFeature[]{
                //    输出key是包含双引号
//                SerializerFeature.QuoteFieldNames,
                //    是否输出为null的字段,若为null 则显示该字段
//                SerializerFeature.WriteMapNullValue,
                //    数值字段如果为null，则输出为0
                SerializerFeature.WriteNullNumberAsZero,
                //     List字段如果为null,输出为[],而非null
                SerializerFeature.WriteNullListAsEmpty,
                //    字符类型字段如果为null,输出为"",而非null
                SerializerFeature.WriteNullStringAsEmpty,
                //    Boolean字段如果为null,输出为false,而非null
                SerializerFeature.WriteNullBooleanAsFalse,
                //    Date的日期转换器
                SerializerFeature.WriteDateUseDateFormat,
                //    循环引用
                SerializerFeature.DisableCircularReferenceDetect,
        };

        fastJsonConfig.setSerializerFeatures(serializerFeatures);
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));

        //3、在convert中添加配置信息
        this.setFastJsonConfig(fastJsonConfig);
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        InputStream in = inputMessage.getBody();
        FastJsonConfig fastJsonConfig = getFastJsonConfig();
        if (requestDecryptResponseEncryptBodyProcessor != null) {
            String input = requestDecryptResponseEncryptBodyProcessor.decryptRequestBody(inputMessage, fastJsonConfig.getCharset());

            byte[] bytes = input.getBytes(fastJsonConfig.getCharset());
            return JSON.parseObject(bytes, 0, bytes.length, fastJsonConfig.getCharset(), type, fastJsonConfig.getFeatures());
        }
        return JSON.parseObject(in, fastJsonConfig.getCharset(), type, fastJsonConfig.getFeatures());
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        ByteArrayOutputStream outnew = new ByteArrayOutputStream();
        FastJsonConfig fastJsonConfig = getFastJsonConfig();

        if (requestDecryptResponseEncryptBodyProcessor != null) {
            String jsonString = JSON.toJSONString(obj,
                    fastJsonConfig.getSerializeConfig(),
                    fastJsonConfig.getSerializeFilters(),
                    fastJsonConfig.getDateFormat(),
                    JSON.DEFAULT_GENERATE_FEATURE,
                    fastJsonConfig.getSerializerFeatures());
            obj = requestDecryptResponseEncryptBodyProcessor.encryptResponseBody(jsonString, headers, fastJsonConfig.getCharset());
        }

        int len = JSON.writeJSONString(outnew,
                fastJsonConfig.getCharset(),
                obj,
                fastJsonConfig.getSerializeConfig(),
                fastJsonConfig.getSerializeFilters(),
                fastJsonConfig.getDateFormat(),
                JSON.DEFAULT_GENERATE_FEATURE,
                fastJsonConfig.getSerializerFeatures());
        headers.setContentLength(len);
        OutputStream out = outputMessage.getBody();
        outnew.writeTo(out);
        outnew.close();

    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream in = inputMessage.getBody();
        FastJsonConfig fastJsonConfig = getFastJsonConfig();
        if (requestDecryptResponseEncryptBodyProcessor != null) {
            String input = requestDecryptResponseEncryptBodyProcessor.decryptRequestBody(inputMessage, fastJsonConfig.getCharset());
            return JSON.parseObject(input.getBytes(fastJsonConfig.getCharset()), 0, input.length(), fastJsonConfig.getCharset(), clazz, fastJsonConfig.getFeatures());
        }
        return JSON.parseObject(in, fastJsonConfig.getCharset(), clazz, fastJsonConfig.getFeatures());
    }

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 重新排列处理方法，不然map会先处理，从而加密不了数据
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>(requestMappingHandlerAdapter.getReturnValueHandlers());

        int requestResponseBodyMethodProcessorIndex = 0;
        int requestDecryptResponseEncryptBodyMethodProcessorIndex = 0;
        RequestDecryptResponseEncryptBodyMethodProcessor requestDecryptResponseEncryptBodyMethodProcessor = null;
        for (int i = 0, length = handlers.size(); i < length; i++) {
            HandlerMethodReturnValueHandler handler = handlers.get(i);
            if (handler instanceof RequestDecryptResponseEncryptBodyMethodProcessor) {
                requestDecryptResponseEncryptBodyMethodProcessor = (RequestDecryptResponseEncryptBodyMethodProcessor) handler;
                requestDecryptResponseEncryptBodyMethodProcessorIndex = i;
            } else if (handler instanceof RequestResponseBodyMethodProcessor) {
                requestResponseBodyMethodProcessorIndex = i;
            }

        }

        if (requestDecryptResponseEncryptBodyMethodProcessor != null) {
            handlers.remove(requestDecryptResponseEncryptBodyMethodProcessorIndex);
            handlers.add(requestResponseBodyMethodProcessorIndex + 1, requestDecryptResponseEncryptBodyMethodProcessor);
        }

        requestMappingHandlerAdapter.setReturnValueHandlers(handlers);

        //
        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>(requestMappingHandlerAdapter.getArgumentResolvers());
        RequestDecryptResponseEncryptBodyMethodProcessor requestDecryptResponseEncryptBodyMethodProcessor2 = null;
        for (int i = 0, length = argumentResolvers.size(); i < length; i++) {
            HandlerMethodArgumentResolver argumentResolver = argumentResolvers.get(i);
            if (argumentResolver instanceof RequestDecryptResponseEncryptBodyMethodProcessor) {
                requestDecryptResponseEncryptBodyMethodProcessor2 = (RequestDecryptResponseEncryptBodyMethodProcessor) argumentResolver;
                requestDecryptResponseEncryptBodyMethodProcessorIndex = i;
            } else if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                requestResponseBodyMethodProcessorIndex = i;
            }
        }

        if (requestDecryptResponseEncryptBodyMethodProcessor2 != null) {
            argumentResolvers.remove(requestDecryptResponseEncryptBodyMethodProcessorIndex);
            argumentResolvers.add(requestResponseBodyMethodProcessorIndex + 1, requestDecryptResponseEncryptBodyMethodProcessor2);
        }

        requestMappingHandlerAdapter.setArgumentResolvers(argumentResolvers);

    }

    @Override
    public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        super.write(o, type, contentType, outputMessage);
    }
}
