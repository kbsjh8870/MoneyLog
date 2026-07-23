package org.example.backend.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.common.response.ApiResponse;
import org.example.backend.statistics.dto.StatisticsResponse;
import org.example.backend.statistics.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private static final Long TEMP_USER_ID = 1L;   // TODO: 로그인 사용자로 교체

    @GetMapping("/monthly")
    public ApiResponse<StatisticsResponse> monthly(@RequestParam String yearMonth) {
        return ApiResponse.success("월별 통계 조회 성공", statisticsService.monthly(TEMP_USER_ID, yearMonth));
    }
}
