package com.sillador.strecs.services.impl;

import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.ResponseCode;

public class BaseService {
    protected BaseResponse error(String message){
        return new BaseResponse().build(ResponseCode.ERROR, message);
    }

    protected BaseResponse success(String message){
        return new BaseResponse().build(ResponseCode.SUCCESS, message);
    }

    protected BaseResponse success(){
        return new BaseResponse().build(ResponseCode.SUCCESS, "Success");
    }
}
