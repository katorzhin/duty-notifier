package com.notification.dutynotifier.service.dutyService;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.exception.DutyNotFoundException;
import com.notification.dutynotifier.exception.UserNotFoundException;
import com.notification.dutynotifier.mapper.DutyMapper;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.userRepository.UserRepository;
import com.notification.dutynotifier.specification.DutySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DutyService {

    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;
    private final DutyMapper dutyMapper;

    public DutyResponse create(DutyRequest request) {

        List<User> users = getUsers(request.getUserIds());

        Duty duty = Duty.builder()
                .users(users)
                .dutyDate(request.getDutyDate())
                .build();

        if (request.getUserIds().size() !=
                new HashSet<>(request.getUserIds()).size()) {

            throw new IllegalArgumentException("Duplicate users selected");
        }

        Duty savedDuty = dutyRepository.save(duty);

        return dutyMapper.toResponse(savedDuty);
    }

    public DutyResponse update(Long id, DutyRequest request) {

        Duty duty = dutyRepository.findById(id)
                .orElseThrow(() -> new DutyNotFoundException("Duty not found"));

        List<User> users = getUsers(request.getUserIds());

        duty.setDutyDate(request.getDutyDate());
        duty.setUsers(users);

        Duty updatedDuty = dutyRepository.save(duty);

        return dutyMapper.toResponse(updatedDuty);
    }

    public void delete(Long id) {

        Duty duty = dutyRepository.findById(id)
                .orElseThrow(() ->
                        new DutyNotFoundException("Duty not found"));

        dutyRepository.delete(duty);
    }

    private List<User> getUsers(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            throw new UserNotFoundException("Some users were not found");
        }

        return users;
    }

    public Page<DutyResponse> getAll(LocalDate from, LocalDate to,
                                     List<Long> userIds, Pageable pageable) {

        Specification<Duty> spec = Specification.allOf();

        if (from != null && to != null) {
            spec = spec.and(DutySpecification.dateBetween(from, to));
        }

        if (userIds != null && !userIds.isEmpty()) {

            spec = spec.and(DutySpecification.hasUsers(userIds));
        }

        return dutyRepository.findAll(spec, pageable).map(dutyMapper::toResponse);
    }

    public List<DutyResponse> getTodayDuty() {
        return dutyRepository.findByDutyDate(LocalDate.now())
                .stream()
                .map(dutyMapper::toResponse)
                .toList();
    }

    public List<DutyResponse> getNext3Days() {
        return dutyRepository.findByDutyDateBetweenOrderByDutyDateAsc(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3))
                .stream()
                .map(dutyMapper::toResponse)
                .toList();
    }

    public List<Duty> findTodayDuties() {
        return dutyRepository.findByDutyDate(
                LocalDate.now()
        );
    }

    public List<Duty> findNext3DayDuties() {
        return dutyRepository.findByDutyDateBetweenOrderByDutyDateAsc(
                LocalDate.now(),
                LocalDate.now().plusDays(3)
        );
    }
}