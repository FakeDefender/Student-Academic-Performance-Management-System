package com.fx.backend.mapper;

import com.fx.backend.domain.entity.TimePeriod;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimePeriodMapper {
    int insert(TimePeriod tp);
    int update(TimePeriod tp);
    int deleteById(@Param("id") Long id);
    Optional<TimePeriod> findById(@Param("id") Long id);
    List<TimePeriod> listActive();
    int countActiveNow(@Param("now") LocalDateTime now);
}


