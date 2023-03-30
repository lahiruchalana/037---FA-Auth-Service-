package com.cloudofgoods.auth.controller;

import com.cloudofgoods.auth.dto.ClientDTO;
import com.cloudofgoods.auth.service.ClientDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v5/oauth/client")
public class ClientController {

    final ClientDetailService clientDetailService;

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ClientDTO> registerClient(@RequestBody ClientDTO clientDTO) {
      log.info("LOG:: ClientController registerClient");
        return ResponseEntity.ok(clientDetailService.saveClient(clientDTO));
    }

    @RequestMapping(value = "/block", method = RequestMethod.GET)
    public ResponseEntity<String> blockClient(HttpServletRequest request) {
        log.info("LOG:: ClientController blockClient");
        String clientId = request.getHeader("clientId");
        return ResponseEntity.ok(clientDetailService.blockClient(clientId));

    }
}
