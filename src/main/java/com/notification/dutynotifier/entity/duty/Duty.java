package com.notification.dutynotifier.entity.duty;

import com.notification.dutynotifier.entity.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "duties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Duty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dutyDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "duty_employees", joinColumns =
    @JoinColumn(name = "duty_id"),
            inverseJoinColumns =
            @JoinColumn(name = "employee_id")
    )
    private List<Employee> employees;
}