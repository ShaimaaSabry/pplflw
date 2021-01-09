package com.pplflw.employee.web;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.pplflw.employee.domain.Employee;
import com.pplflw.employee.domain.EmployeeState;
import com.pplflw.employee.dto.CreateEmployeeRequest;
import com.pplflw.employee.dto.EmployeeEvent;
import com.pplflw.employee.dto.EmployeeResponse;
import com.pplflw.employee.dto.UpdateEmployeeStateRequest;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class EmployeeControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestEntityManager entityManager;
	
	@MockBean
	private KafkaTemplate<String, Employee> kafkaTemplate;

	@Test
	public void whenCalled_thenReturn200AndEmployeeList() throws Exception {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ADDED);
		employee = entityManager.persist(employee);

		RequestBuilder request = MockMvcRequestBuilders.get("/employees");
		MvcResult result = mockMvc.perform(request).andReturn();

		Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		List<EmployeeResponse> employeeResponseList = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<List<EmployeeResponse>>() {
				});
		Assertions.assertEquals(1, employeeResponseList.size());
	}

	@Test
	public void whenEmployeeIdIsValid_thenReturn200AndEmployee() throws Exception {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ADDED);
		employee = entityManager.persist(employee);

		RequestBuilder request = MockMvcRequestBuilders.get("/employees/{employeeId}", employee.getId());
		MvcResult result = mockMvc.perform(request).andReturn();

		Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		EmployeeResponse employeeRespose = objectMapper.readValue(result.getResponse().getContentAsString(),
				EmployeeResponse.class);
		Assertions.assertEquals(employee.getId(), employeeRespose.getId());
	}

	@Test
	public void whenEmployeeIdIsInvalid_thenGetOneReturn404() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/employees/{employeeId}", 1);
		MvcResult result = mockMvc.perform(request).andReturn();

		Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
	}

	@Test
	public void whenCreateEmployeeRequestIsValid_thenCreateEmployeeAndReturn201() throws Exception {
		CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("Test", "Employee", 25);
		RequestBuilder request = MockMvcRequestBuilders.post("/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createEmployeeRequest));
		MvcResult result = mockMvc.perform(request).andReturn();

		Assertions.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		Assertions.assertTrue(result.getResponse().getHeader(HttpHeaders.LOCATION).contains("/employees/"));
		EmployeeResponse employeeRespose = objectMapper.readValue(result.getResponse().getContentAsString(),
				EmployeeResponse.class);
		Employee employee = entityManager.find(Employee.class, employeeRespose.getId());
		Assertions.assertEquals(createEmployeeRequest.getFirstName(), employee.getFirstName());
		Assertions.assertEquals(createEmployeeRequest.getLastName(), employee.getLastName());
		Assertions.assertEquals(createEmployeeRequest.getAge(), employee.getAge());
		Assertions.assertEquals(EmployeeState.ADDED, employee.getState());
	}

	@Test
	public void whenCreateEmployeeRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/employees").contentType(MediaType.APPLICATION_JSON)
				.content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("firstName"));
		Assertions.assertTrue(errors.get("fieldErrors").get("firstName").contains("required"));
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("lastName"));
		Assertions.assertTrue(errors.get("fieldErrors").get("lastName").contains("required"));
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("age"));
		Assertions.assertTrue(errors.get("fieldErrors").get("age").contains("required"));
	}

	@Test
	public void whenUpdateEmployeeStateRequestIsValid_thenUpdateEmployeeStateAndReturn200() throws Exception {
		Employee employee = new Employee(null, "Test", "Employee", 25, EmployeeState.ADDED);
		employee = entityManager.persist(employee);

		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.CHECK);
		RequestBuilder request = MockMvcRequestBuilders.put("/employees/{employeeId}/state", employee.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateEmployeeStateRequest));
		MvcResult result = mockMvc.perform(request).andReturn();

		Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		employee = entityManager.find(Employee.class, employee.getId());
		Assertions.assertEquals(EmployeeState.INCHECK, employee.getState());
	}

	@Test
	public void whenUpdateEmployeeStateRequestIsInvalid_thenReturn400AndErrors() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.put("/employees/{employeeId}/state", 1)
				.contentType(MediaType.APPLICATION_JSON).content("{}");
		MvcResult result = mockMvc.perform(request).andReturn();

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
		Map<String, Map<String, String>> errors = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		Assertions.assertTrue(errors.get("fieldErrors").containsKey("event"));
		Assertions.assertTrue(errors.get("fieldErrors").get("event").contains("required"));
	}

	@Test
	public void whenEmployeeIdIsInvalid_thenPutReturn404() throws Exception {
		UpdateEmployeeStateRequest updateEmployeeStateRequest = new UpdateEmployeeStateRequest(EmployeeEvent.CHECK);
		RequestBuilder request = MockMvcRequestBuilders.put("/employees/{employeeId}/state", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateEmployeeStateRequest));
		MvcResult result = mockMvc.perform(request).andReturn();

		Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
	}
}
