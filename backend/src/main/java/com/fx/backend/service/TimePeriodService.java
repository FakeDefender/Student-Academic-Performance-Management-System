package com.fx.backend.service;

import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.TimePeriod;
import com.fx.backend.mapper.TimePeriodMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimePeriodService {
    private final TimePeriodMapper timePeriodMapper;

    public TimePeriodService(TimePeriodMapper timePeriodMapper) { this.timePeriodMapper = timePeriodMapper; }

    @Transactional
    public Long create(TimePeriod tp) { timePeriodMapper.insert(tp); return tp.getId(); }

    @Transactional
    public void update(TimePeriod tp) { timePeriodMapper.update(tp); }

    @Transactional
    public void delete(Long id) { timePeriodMapper.deleteById(id); }

    public List<TimePeriod> listActive() { return timePeriodMapper.listActive(); }

    public void assertOpenNow() {
        int cnt = timePeriodMapper.countActiveNow(LocalDateTime.now());
        if (cnt <= 0) {
            throw new BusinessException("TIME_WINDOW_CLOSED", "当前不在成绩查询开放时间段内");
        }
    }
}


