package com.notification.dutynotifier.specification;

import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.user.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class DutySpecification {

    public static Specification<Duty> dateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) ->
                cb.between(root.get("dutyDate"), from, to);
    }

    public static Specification<Duty> hasUsers(List<Long> userIds) {
        return (root, query, cb) -> {

            if (query != null) {
                query.distinct(true);
            }

            Join<Duty, User> users = root.join("users");

            return users.get("id").in(userIds);
        };
    }
}