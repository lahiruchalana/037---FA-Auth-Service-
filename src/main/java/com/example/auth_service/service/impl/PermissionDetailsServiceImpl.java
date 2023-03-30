package com.cloudofgoods.auth.service.impl;

import com.cloudofgoods.auth.dao.PermissionDetailsRepository;
import com.cloudofgoods.auth.entity.Permission;
import com.cloudofgoods.auth.service.PermissionDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionDetailsServiceImpl implements PermissionDetailsService {
    final PermissionDetailsRepository permissionDetailsRepository;

    @Override
    public String savePermission(String permissionName) {
        log.info("Log::PermissionDetailsServiceImpl.savePermission");
        permissionDetailsRepository.save(new Permission(null, permissionName));
        return permissionName;
    }

    @Override
    public boolean delete(Integer permissionId) {
        log.info("Log::PermissionDetailsServiceImpl.delete ");
        return permissionDetailsRepository.deletePermissionById(permissionId);
    }
}
