package com.cloudofgoods.auth.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface QueryDAO {
    void updateThrottling(String query) ;
    void createThrottling(String query) ;
    void updateThrottlingAndLockUserAccount(String query) ;
}
