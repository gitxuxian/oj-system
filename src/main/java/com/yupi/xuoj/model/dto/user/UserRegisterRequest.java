package com.yupi.xuoj.model.dto.user;

import java.io.Serializable;
import lombok.Data;


@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userName;

    private String userPassword;

    private String checkPassword;

    private String userEmail;

    private String phone;

    private String code;
}
