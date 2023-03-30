/**
 * @author - Chamath_Wijayarathna
 * Date :6/17/2022
 */


package com.cloudofgoods.auth.service.impl;


import com.cloudofgoods.auth.entity.User;

import lombok.Getter;

@Getter
public class LoginAttemptsServiceBOImpl   {

    private final User appUser;

    public LoginAttemptsServiceBOImpl(User appUser) {
        this.appUser = appUser;
    }

}
