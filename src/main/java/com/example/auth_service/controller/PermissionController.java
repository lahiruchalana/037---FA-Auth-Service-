package com.cloudofgoods.auth.controller;

import com.cloudofgoods.auth.service.PermissionDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v5/oauth/permission")
public class PermissionController {
    final PermissionDetailsService permissionDetailsService;

    @RequestMapping(value = "/create/{permissionName}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String createPermission(@PathVariable(name = "permissionName") String permissionName) {
        return permissionDetailsService.savePermission(permissionName);
    }

    @RequestMapping(value = "/delete/{permissionId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public boolean removePermission(@PathVariable(name = "permissionId") Integer permissionId) {
        log.info("LOG:: ClientController blockClient");
        return permissionDetailsService.delete(permissionId);
    }
}
