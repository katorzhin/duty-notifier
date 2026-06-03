package com.notification.dutynotifier.service.dutyService;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.user.User;
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

    public Duty create(DutyRequest request) {

        List<User> users = userRepository.findAllById(request.getUserIds());

        Duty duty = Duty.builder()
                        .users(users)
                        .dutyDate(request.getDutyDate())
                        .build();

        return dutyRepository.save(duty);
    }

    public List<Duty> getAll() {
        return dutyRepository.findAll();
    }

    public List<Duty> getTodayDuty() {
        return dutyRepository.findByDutyDate(
                LocalDate.now()
        );
    }

    public List<Duty> getNext5Days() {
        return dutyRepository.findByDutyDateBetween(
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );
    }
}