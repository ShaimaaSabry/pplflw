package com.pplflw.employee.dto;

import com.pplflw.employee.domain.EmployeeState;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeResponse {
    private long id;
    private String firstName;
    private String lastName;
    private int age;
    private EmployeeState state;
}
