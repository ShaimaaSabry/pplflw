package com.pplflw.employee.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pplflw.employee.dto.CreateEmployeeRequest;
import com.pplflw.employee.dto.EmployeeResponse;
import com.pplflw.employee.dto.UpdateEmployeeStateRequest;
import com.pplflw.employee.exception.InvalidIdException;
import com.pplflw.employee.service.EmployeeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/employees")
@Api(tags = "Employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
	this.employeeService = employeeService;
    }

    @GetMapping
    @ApiOperation(value = "Get Employee List", notes = "Gell a list of all employees.")
    public List<EmployeeResponse> getAll() {
	return employeeService.getAll();
    }

    @GetMapping("{employeeId}")
    @ApiOperation(value = "Get Employee", notes = "Get employee by id.")
    public EmployeeResponse getOne(@PathVariable("employeeId") long employeeId) throws InvalidIdException {
	return employeeService.getOne(employeeId);
    }

    @PostMapping
    @ApiOperation(value = "Create Employee", notes = "Create new employee.")
    public ResponseEntity<EmployeeResponse> create(
	    @Validated @RequestBody CreateEmployeeRequest createEmployeeRequest) {
	EmployeeResponse employee = employeeService.create(createEmployeeRequest);

	URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{employeeId}").buildAndExpand(employee.getId()).toUri();
	return ResponseEntity.created(location).body(employee);
    }

    @PutMapping("{employeeId}/state")
    @ApiOperation(value = "Update Employee State", notes = "Update employee state. Available employee event values: {CHECK, APPROVE, ACTIVATE}")
    public EmployeeResponse updateState(@PathVariable("employeeId") long employeeId, @Validated @RequestBody UpdateEmployeeStateRequest updateEmployeeStateRequest) throws InvalidIdException {
	return employeeService.updateState(employeeId, updateEmployeeStateRequest);
    }
}
