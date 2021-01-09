package com.pplflw.employee.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateEmployeeRequest {
    @NotBlank(message = "First Name is required.")
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank(message = "Last Name is required.")
    @Size(min = 2, max = 20)
    private String lastName;

    @NotNull(message = "Age is required.")
    @Min(18)
    @Max(70)
    private Integer age;
}
