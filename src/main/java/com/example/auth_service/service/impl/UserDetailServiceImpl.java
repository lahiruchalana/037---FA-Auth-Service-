package com.cloudofgoods.auth.service.impl;


import com.cloudofgoods.auth.dao.UserDetailRepository;
import com.cloudofgoods.auth.dto.AccountLockUnlockDTO;
import com.cloudofgoods.auth.dto.UserDTO;
import com.cloudofgoods.auth.dto.UserRegisterDTO;
import com.cloudofgoods.auth.entity.Role;
import com.cloudofgoods.auth.entity.User;
import com.cloudofgoods.auth.enumpackage.AuthenticationType;
import com.cloudofgoods.auth.model.AuthUserDetail;
import com.cloudofgoods.auth.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService, UserDetailService {

    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegisterDTO registerUser(UserRegisterDTO registrationRequest) {
        log.info("LOG::UserDetailServiceImpl registerUser " + registrationRequest.getUsername());
        User user = new User();
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        user.setEmail(registrationRequest.getEmail());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setAuthType(AuthenticationType.Manual);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        userDetailRepository.save(user);

        return registrationRequest;
    }

    @Override
    public List<UserDTO> findAllUsersWithPagination(String startNumber, String sizeNumber) {
        List<UserDTO> userDTOS = new ArrayList<>();
        log.info("LOG::UserDetailServiceImpl findAllUsers");
        int start = Integer.parseInt(startNumber);
        int end = Integer.parseInt(sizeNumber);
        Pageable firstPageWithTwoElements = PageRequest.of(start, end);
        Page<User> userList = userDetailRepository.findAll(firstPageWithTwoElements);
        for (User user : userList) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setEnabled(user.isEnabled());
            userDTO.setAccountNonExpired(user.isAccountNonExpired());
            userDTO.setCredentialsNonExpired(user.isCredentialsNonExpired());
            userDTO.setAccountNonLocked(user.isAccountNonLocked());
            userDTOS.add(userDTO);
        }
        return userDTOS;
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<UserDTO> userDTOS = new ArrayList<>();
        log.info("LOG::UserDetailServiceImpl findAllUsers");
        List<User> userList = userDetailRepository.findAll();
        for (User user : userList) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setEnabled(user.isEnabled());
            userDTO.setAccountNonExpired(user.isAccountNonExpired());
            userDTO.setCredentialsNonExpired(user.isCredentialsNonExpired());
            userDTO.setAccountNonLocked(user.isAccountNonLocked());
            userDTOS.add(userDTO);
        }
        return userDTOS;
    }


    @Override
    public Optional<UserDTO> getUserById(String userName) {
        log.info("LOG::UserDetailServiceImpl getUserById");
        Optional<User> byUsername = userDetailRepository.findByEmail(userName);
        UserDTO userDTO;
        if (byUsername.isPresent()) {
            userDTO = new UserDTO();
            userDTO.setId(byUsername.get().getId());
            userDTO.setUsername(byUsername.get().getUsername());
            userDTO.setEmail(byUsername.get().getEmail());
            userDTO.setEnabled(byUsername.get().isEnabled());
            userDTO.setAccountNonExpired(byUsername.get().isAccountNonExpired());
            userDTO.setCredentialsNonExpired(byUsername.get().isCredentialsNonExpired());
            userDTO.setAccountNonLocked(byUsername.get().isAccountNonLocked());
        } else {
            return Optional.empty();
        }
        return Optional.of(userDTO);
    }

    @Override
    public String removeRoleFromUser(String userName, Role role) {
        log.info("LOG::UserDetailServiceImpl removeRoleFromUser");
        User removeRoleReturnedValue = null;
        Optional<User> userFromUserName = userDetailRepository.findByEmail(userName);
        if (userFromUserName.isPresent()) {
            User existingUserDetails = new User(userFromUserName.get());
            List<Role> list = new ArrayList<>();
            for (Role getUserRoleOneByOne : existingUserDetails.getRoles()) {
                if (!getUserRoleOneByOne.getName().equals(role.getName())) {
                    if (!list.contains(getUserRoleOneByOne)) list.add(getUserRoleOneByOne);
                }
            }
            existingUserDetails.setRoles(null);
            existingUserDetails.setRoles(list);

            removeRoleReturnedValue = userDetailRepository.save(existingUserDetails);
        }
        if (!ObjectUtils.isEmpty(removeRoleReturnedValue)) {
            return "User Remove Successfully";
        } else {
            return "User Remove Fail";
        }
    }

    @Override
    public String lockOrUnlockUserAccount(AccountLockUnlockDTO accountLockUnlockDTO) {
        log.info("LOG::UserDetailServiceImpl lockOrUnlockUserAccount");
        Optional<User> user = userDetailRepository.findByEmail(accountLockUnlockDTO.getEmail());
        if (user.isPresent()) {
            User userDetails = new User(user.get());
            userDetails.setAccountNonLocked(accountLockUnlockDTO.isAccountLockUnlockStatus());
            User saveUser = userDetailRepository.save(userDetails);
            if (!ObjectUtils.isEmpty(saveUser)) {
                if (accountLockUnlockDTO.isAccountLockUnlockStatus()) {
                    return "User Unlock Successfully";
                } else {
                    return "User Lock Successfully";
                }
            } else {
                return "User Lock Fail";
            }
        } else {
            return "User Unknown";
        }
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        log.info("Log::User Details Service . loadUserByUserName");
        Optional<User> optionalUser = userDetailRepository.findByUsername(name);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("User name is not in the database"));
        User user = optionalUser.get();
        AuthUserDetail userDetails = new AuthUserDetail(user);
        new AccountStatusUserDetailsChecker().check(userDetails);
        return userDetails;

    }

}