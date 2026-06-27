package com.notification.dutynotifier.repository.dutyRepository;

import com.notification.dutynotifier.entity.duty.Duty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface DutyRepository extends JpaRepository<Duty, Long>, JpaSpecificationExecutor<Duty> {

    List<Duty> findByDutyDate(
            LocalDate dutyDate
    );

    List<Duty> findByDutyDateBetweenOrderByDutyDateAsc(
            LocalDate start,
            LocalDate end
    );

    boolean existsByUsers_Id(Long userId);

}