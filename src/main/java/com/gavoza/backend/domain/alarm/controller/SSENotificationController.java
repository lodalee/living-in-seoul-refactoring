package com.gavoza.backend.domain.alarm.controller;

import com.gavoza.backend.domain.alarm.service.SSENotificationService;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SSENotificationController {

    private final SSENotificationService SSENotificationService;

    @CrossOrigin
    @GetMapping(value = "/notice", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return SSENotificationService.addSeeEmitter(userDetails.getUser().getId());
    }
}
