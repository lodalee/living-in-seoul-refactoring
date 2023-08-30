package com.gavoza.backend.domain.report.controller;

import com.gavoza.backend.domain.report.ReportType;
import com.gavoza.backend.domain.report.service.ReportService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    //신고
    @GetMapping("/report/{targetId}")
    public MessageResponseDto report(@PathVariable Long targetId,
                                     @RequestParam ReportType reportType,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return reportService.report(targetId,reportType, user);
    }
}
