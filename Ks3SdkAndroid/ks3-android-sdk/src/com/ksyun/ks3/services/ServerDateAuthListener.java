package com.ksyun.ks3.services;

/**
 * Created by zhaotao on 4/14/17.
 */

public abstract class ServerDateAuthListener implements AuthListener{
    public abstract AuthResult onCalculateAuthWithServerDate(String httpMethod, String ContentType,
                                                             String Date, String ContentMD5, String Resource, String Headers);

    @Override
    public String onCalculateAuth(String httpMethod, String ContentType, String Date, String ContentMD5, String Resource, String Headers) {
        return null;
    }
}
