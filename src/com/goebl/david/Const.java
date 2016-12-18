package com.goebl.david;

/**
 * Constant values and strings.
 */
class Const {
    static final String DEFAULT_USER_AGENT = "com.goebl.david.Webb/1.0";
    static final String APP_FORM = "application/x-www-form-urlencoded";
    static final String APP_JSON = "application/json";
    static final String APP_BINARY = "application/octet-stream";
    static final String TEXT_PLAIN = "text/plain";
    static final String HDR_CONTENT_TYPE = "Content-Type";
    static final String HDR_CONTENT_ENCODING = "Content-Encoding";
    static final String HDR_ACCEPT_ENCODING = "Accept-Encoding";
    static final String HDR_ACCEPT = "Accept";
    static final String HDR_USER_AGENT = "User-Agent";
    static final String UTF8 = "utf-8";

    static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    static final Class BYTE_ARRAY_CLASS = EMPTY_BYTE_ARRAY.getClass();
    /** Minimal number of bytes the compressed content must be smaller than uncompressed */
    static final int MIN_COMPRESSED_ADVANTAGE = 80;
}
