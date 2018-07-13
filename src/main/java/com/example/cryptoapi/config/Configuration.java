package com.example.cryptoapi.config;

import com.example.cryptoapi.springmvc.extension.*;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public RequestDecryptResponseEncryptBodyProcessor requestDecryptResponseEncryptBodyProcessor() {
        RequestDecryptResponseEncryptBodyProcessor processor = new RequestDecryptResponseEncryptBodyProcessorImpl();
        return processor;
    }

    @Bean
    public DecryptEncryptFastJsonHttpMessageConverter decryptEncryptFastJsonHttpMessageConverter(RequestDecryptResponseEncryptBodyProcessor requestDecryptResponseEncryptBodyProcessor) {
        DecryptEncryptFastJsonHttpMessageConverter decryptEncryptFastJsonHttpMessageConverter = new DecryptEncryptFastJsonHttpMessageConverter();
        decryptEncryptFastJsonHttpMessageConverter.setRequestDecryptResponseEncryptBodyProcessor(requestDecryptResponseEncryptBodyProcessor);
//        List<MediaType> mediaTypes = new ArrayList<>();
//        mediaTypes.add(new MediaType(""));
//        decryptEncryptFastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        return decryptEncryptFastJsonHttpMessageConverter;
    }

    @Bean
    public RequestDecryptResponseEncryptBodyMethodProcessor requestDecryptResponseEncryptBodyMethodProcessor(DecryptEncryptFastJsonHttpMessageConverter decryptEncryptFastJsonHttpMessageConverter) {
        List<HttpMessageConverter<?>> httpMessageConverterList = new ArrayList<>();
        httpMessageConverterList.add(decryptEncryptFastJsonHttpMessageConverter);
        RequestDecryptResponseEncryptBodyMethodProcessor requestDecryptResponseEncryptBodyMethodProcessor = new RequestDecryptResponseEncryptBodyMethodProcessor(httpMessageConverterList);
        return requestDecryptResponseEncryptBodyMethodProcessor;
    }

    /**
     * 方式一：添加自定义HTTPMessageConverter
     * 这种方式无效,自定义的注解ResponseEncryptBody没有起作用，请求没有走自定义的HttpMessageConverter
     *
     */
//    @Bean
//    public HttpMessageConverters customConverters() {
//        HttpMessageConverter<?> additional = new DecryptEncryptFastJsonHttpMessageConverter();
//        return new HttpMessageConverters(additional);
//    }


    /**
     * 方式二： 添加自定义HTTPMessageConverter
     * 有效的,但是请求结果多出了反斜杠
     */
    @Bean
    public ExtendWebMvcConfig extendWebMvcConfig(){
        return new ExtendWebMvcConfig();
    }

}
