package com.cloudofgoods.auth.service;

import com.cloudofgoods.auth.entity.User;

public interface LoginAttemptsUserServicesBO {

//    Call From Configurations
    void increaseFailedAttempts(User user);
    void lock(User user);
    User findByUsername(String userName);
    void updateAuthenticationType(User appUser);

}
