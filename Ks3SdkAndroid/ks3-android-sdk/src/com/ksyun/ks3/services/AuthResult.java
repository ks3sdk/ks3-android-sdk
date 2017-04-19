package com.ksyun.ks3.services;

import java.util.Date;

/**
 * Created by zhaotao on 4/14/17.
 */

public class AuthResult {
    String date;
    String token;
    public AuthResult(String d, String t){
        date = d;
        token = t;
    }
    public String getDate(){ return date; }
    public String getToken() { return token; }
}
