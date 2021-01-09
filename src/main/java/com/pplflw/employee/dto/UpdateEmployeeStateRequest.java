package com.pplflw.employee.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateEmployeeStateRequest {
    @NotNull(message = "Event is required.")
    private EmployeeEvent event;
}
