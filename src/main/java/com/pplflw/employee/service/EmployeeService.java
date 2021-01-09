package com.pplflw.employee.service;

import java.util.List;

import com.pplflw.employee.dto.CreateEmployeeRequest;
import com.pplflw.employee.dto.EmployeeResponse;
import com.pplflw.employee.dto.UpdateEmployeeStateRequest;
import com.pplflw.employee.exception.InvalidIdException;

public interface EmployeeService {
    List<EmployeeResponse> getAll();

    EmployeeResponse getOne(long employeeId) throws InvalidIdException;

    EmployeeResponse create(CreateEmployeeRequest createEmployeeRequest);

    EmployeeResponse updateState(long employeeId, UpdateEmployeeStateRequest updateEmployeeStateRequest) throws InvalidIdException;
}
