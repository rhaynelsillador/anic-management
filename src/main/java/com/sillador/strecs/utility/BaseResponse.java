package com.sillador.strecs.utility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class BaseResponse {

    private static final String SUCCESS = "Success";

    public BaseResponse build(Object data){
        this.data = data;
        build(null, null);
        return this;
    }

    public BaseResponse build(ResponseCode code, String message){
        if(code == null){
            code = ResponseCode.SUCCESS;
        }
        if(message == null || message.isEmpty()){
            this.message = SUCCESS;
        }else{
            this.message = message;
        }

        this.code = code.getCode();
        this.status = code.name();

        return this;
    }

    public BaseResponse success(){
        this.code = ResponseCode.SUCCESS.getCode();
        this.status = ResponseCode.SUCCESS.name();
        this.message = SUCCESS;
        return this;
    }


    private Object data;
    private String status;
    private String message;
    private int code;
    @Setter
    private Page page;


}
