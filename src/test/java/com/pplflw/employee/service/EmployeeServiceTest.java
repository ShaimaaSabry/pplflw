package com.pplflw.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import com.pplflw.employee.domain.Employee;
import com.pplflw.employee.domain.EmployeeState;
import com.pplflw.employee.dto.CreateEmployeeRequest;
import com.pplflw.employee.dto.EmployeeEvent;
import com.pplflw.employee.dto.EmployeeResponse;
import com.pplflw.employee.dto.UpdateEmployeeStateRequest;
import com.pplflw.employee.exception.InvalidIdException;
import com.pplflw.employee.service.EmployeeService;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
public class EmployeeServiceTest {
	@Autowired
	EmployeeService employeeService;

	@Autowired
	private TestEntityManager entityManager;

	@MockBean
	private KafkaTemplate<String, Employee> kafkaTemplate;

	@Test
	public void whenCalled_thenReturnEmployeeList() {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ADDED);
		employee = entityManager.persist(employee);

		List<EmployeeResponse> employeeResponseList = employeeService.getAll();

		Assertions.assertEquals(1, employeeResponseList.size());
	}

	@Test
	public void whenEmployeeIdIsValid_thenReturnEmployee() throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ADDED);
		employee = entityManager.persist(employee);

		EmployeeResponse employeeResponse = employeeService.getOne(employee.getId());

		Assertions.assertEquals(employee.getId(), employeeResponse.getId());
	}

	@Test
	public void whenEmployeeIdIsInvalid_thenGetOneThrowInvalidIdException() {
		Assertions.assertThrows(InvalidIdException.class, () -> {
			employeeService.getOne(1);
		});
	}

	@Test
	public void whenCreateEmployeeRequestIsValid_thenCreateEmployee() {
		CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("Test", "Employee", 25);
		EmployeeResponse employeeResponse = employeeService.create(createEmployeeRequest);

		Employee employee = entityManager.find(Employee.class, employeeResponse.getId());
		Assertions.assertEquals(createEmployeeRequest.getFirstName(), employee.getFirstName());
		Assertions.assertEquals(createEmployeeRequest.getLastName(), employee.getLastName());
		Assertions.assertEquals(createEmployeeRequest.getAge(), employee.getAge());
		Assertions.assertEquals(EmployeeState.ADDED, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsAddedAndEmployeeEventIsCheck_thenUpdateEmployeeStateToIncheck()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ADDED);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.CHECK);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.INCHECK, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsAddedAndEmployeeEventIsApprove_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ADDED);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.APPROVE);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.ADDED, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsAddedAndEmployeeEventIsActivate_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ADDED);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.ACTIVATE);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.ADDED, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsIncheckAndEmployeeEventIsApprove_thenUpdateEmployeeStateToApproved()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.INCHECK);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.APPROVE);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.APPROVED, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsIncheckAndEmployeeEventIsCheck_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.INCHECK);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.CHECK);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.INCHECK, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsIncheckAndEmployeeEventIsActivate_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.INCHECK);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.ACTIVATE);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.INCHECK, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsApprovedAndEmployeeEventIsActivate_thenUpdateEmployeeStateToActive()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.APPROVED);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.ACTIVATE);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.ACTIVE, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsApprovedAndEmployeeEventIsCheck_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.APPROVED);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.CHECK);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.APPROVED, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsApprovedAndEmployeeEventIsApprove_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.APPROVED);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.APPROVE);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.APPROVED, employee.getState());
	}

	@Test
	public void whenEmployeeStateIsActiveAndEmployeeEventIsCheck_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ACTIVE);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.CHECK);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.ACTIVE, employee.getState());
	}

	public void whenEmployeeStateIsActiveAndEmployeeEventIsApprove_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ACTIVE);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.APPROVE);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.ACTIVE, employee.getState());
	}

	public void whenEmployeeStateIsActiveAndEmployeeEventIsActivate_thenDoNotUpdateEmployeeState()
			throws InvalidIdException {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ACTIVE);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.ACTIVATE);
		employeeService.updateState(employee.getId(), updateEmployeeStateRequest);

		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.ACTIVE, employee.getState());
	}

	@Test
	public void whenEmployeeIdIsInvalid_thenUpdateStateThrowInvalidIdException() {
		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.APPROVE);

		Assertions.assertThrows(InvalidIdException.class, () -> {
			employeeService.updateState(1, updateEmployeeStateRequest);
		});
	}
}
