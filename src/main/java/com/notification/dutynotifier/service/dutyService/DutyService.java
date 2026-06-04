package com.notification.dutynotifier.service.dutyService;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.mapper.DutyMapper;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DutyService {

    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;
    private final DutyMapper dutyMapper;

    public DutyResponse create(DutyRequest request) {

        List<User> users = userRepository.findAllById(request.getUserIds());

        Duty duty = Duty.builder()
                .users(users)
                .dutyDate(request.getDutyDate())
                .build();

        Duty savedDuty = dutyRepository.save(duty);
        return dutyMapper.toResponse(savedDuty);
    }

    public List<DutyResponse> getAll() {
        return dutyRepository.findAll()
                .stream()
                .map(dutyMapper::toResponse)
                .toList();
    }

    public List<DutyResponse> getTodayDuty() {
        return dutyRepository.findByDutyDate(LocalDate.now())
                .stream()
                .map(dutyMapper::toResponse)
                .toList();
    }

    public List<DutyResponse> getNext5Days() {
        return dutyRepository.findByDutyDateBetween(
                        LocalDate.now(),
                        LocalDate.now().plusDays(5))
                .stream()
                .map(dutyMapper::toResponse)
                .toList();
    }

    public List<Duty> findTodayDuties() {
        return dutyRepository.findByDutyDate(
                LocalDate.now()
        );
    }

    public List<Duty> findNext5DayDuties() {
        return dutyRepository.findByDutyDateBetween(
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );
    }
}