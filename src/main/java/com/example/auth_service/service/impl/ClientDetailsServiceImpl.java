package com.cloudofgoods.auth.service.impl;


import com.cloudofgoods.auth.dao.ClientDetailsRepository;
import com.cloudofgoods.auth.dto.ClientDTO;
import com.cloudofgoods.auth.entity.OauthClientDetails;
import com.cloudofgoods.auth.entity.User;
import com.cloudofgoods.auth.service.ClientDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
@Slf4j
public class ClientDetailsServiceImpl implements ClientDetailService {

    ClientDetailsRepository clientDetailsRepository;

    public ClientDetailsServiceImpl() {
    }

    public ClientDetailsServiceImpl(ClientDetailsRepository clientDetailsRepository) {
        this.clientDetailsRepository = clientDetailsRepository;
    }

    @Override
    public ClientDTO saveClient(ClientDTO clientDTO) {
        log.info("LOG::ClientDetailsServiceImpl.saveClient(clientDTO)");
        OauthClientDetails oauthClientDetails = new OauthClientDetails();

        oauthClientDetails.setClientId(clientDTO.getClientId());
        oauthClientDetails.setClientSecret(clientDTO.getClientSecret());
        oauthClientDetails.setWebServerRedirectUri(clientDTO.getWebServerRedirectUri());
        oauthClientDetails.setScope(clientDTO.getScope());
        oauthClientDetails.setAccessTokenValidity(clientDTO.getAccessTokenValidity());
        oauthClientDetails.setRefreshTokenValidity(clientDTO.getRefreshTokenValidity());
        oauthClientDetails.setResourceIds(clientDTO.getResourceIds());
        oauthClientDetails.setAuthorizedGrantTypes(clientDTO.getAuthorizedGrantTypes());
        oauthClientDetails.setAuthorities(clientDTO.getAuthorities());
        oauthClientDetails.setAdditionalInformation(clientDTO.getAdditionalInformation());
        oauthClientDetails.setAutoApprove(clientDTO.getAutoApprove());

        clientDetailsRepository.save(oauthClientDetails);
        return clientDTO;
    }

    @Override
    public String blockClient(String clientId) {
        Optional<OauthClientDetails> user = clientDetailsRepository.findById(clientId);
        if (user.isPresent()) {
            OauthClientDetails userDetails = new OauthClientDetails(user.get());
            userDetails.setAccessTokenValidity(0);
            userDetails.setRefreshTokenValidity(0);
            OauthClientDetails saveUser = clientDetailsRepository.save(userDetails);
            if (!ObjectUtils.isEmpty(saveUser)) {
                return "Fail";
            }else {
                return "Success";
            }
        }else {
            return "fail client Not Found";
        }
    }
}
