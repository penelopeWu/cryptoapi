package com.example.cryptoapi.springmvc.extension;

import org.springframework.http.HttpHeaders;

import java.nio.charset.Charset;

public class RequestDecryptResponseEncryptBodyProcessorImpl extends RequestDecryptResponseEncryptBodyProcessor {
    @Override
    protected String doDecryptRequestBody(String input, HttpHeaders httpHeaders, Charset charset) {

        return super.doDecryptRequestBody(input, httpHeaders, charset);
    }

    @Override
    protected String doEncryptResponseBody(String input, HttpHeaders httpHeaders, Charset charset) {
        return super.doEncryptResponseBody(input, httpHeaders, charset);
    }
}
