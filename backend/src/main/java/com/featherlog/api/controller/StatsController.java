package com.featherlog.api.controller;

import com.featherlog.api.dto.StatsSummaryResponse;
import com.featherlog.api.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/summary")
    public StatsSummaryResponse getSummary() {
        return statsService.getSummary();
    }
}
