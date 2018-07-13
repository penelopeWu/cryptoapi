package com.example.cryptoapi.springmvc.extension;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public abstract class RequestDecryptResponseEncryptBodyProcessor {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public final String decryptRequestBody(HttpInputMessage inputMessage, Charset charset) throws IOException {
        InputStream inputStream = inputMessage.getBody();
        String input = IOUtils.toString(inputStream, charset);
        HttpHeaders httpHeaders = inputMessage.getHeaders();
        return doDecryptRequestBody(input, httpHeaders, charset);
    }

    public final String encryptResponseBody(String input, HttpHeaders httpHeaders, Charset charset) {
        return doEncryptResponseBody(input, httpHeaders, charset);
    }

    protected String doDecryptRequestBody(String input, HttpHeaders httpHeaders, Charset charset) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return new String(decoder.decodeBuffer(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String doEncryptResponseBody(String input, HttpHeaders httpHeaders, Charset charset) {
//        BASE64Encoder encoder = new BASE64Encoder();
//        String encode = encoder.encode(input.getBytes());
//        return encode;
        return input;
    }

}
