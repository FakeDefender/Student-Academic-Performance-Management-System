package com.fx.backend.interfaces;

import com.fx.backend.common.api.ApiResponse;
import com.fx.backend.domain.entity.TimePeriod;
import com.fx.backend.service.TimePeriodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-periods")
public class TimePeriodController {
    private final TimePeriodService timePeriodService;

    public TimePeriodController(TimePeriodService timePeriodService) { this.timePeriodService = timePeriodService; }

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(@RequestBody TimePeriod tp) { return ResponseEntity.ok(ApiResponse.ok(timePeriodService.create(tp))); }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(@PathVariable Long id, @RequestBody TimePeriod tp) { tp.setId(id); timePeriodService.update(tp); return ResponseEntity.ok(ApiResponse.ok(null)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) { timePeriodService.delete(id); return ResponseEntity.ok(ApiResponse.ok(null)); }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TimePeriod>>> active() { return ResponseEntity.ok(ApiResponse.ok(timePeriodService.listActive())); }
}


