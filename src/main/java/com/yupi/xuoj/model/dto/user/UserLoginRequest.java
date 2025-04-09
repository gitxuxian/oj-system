package com.yupi.xuoj.model.dto.user;

import java.io.Serializable;

import lombok.Data;


@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String email;

    private String phone;

    private String userPassword;
}
