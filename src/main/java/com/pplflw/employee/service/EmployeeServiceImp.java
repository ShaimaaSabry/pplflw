package com.pplflw.employee.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import com.pplflw.employee.config.KafkaProperties;
import com.pplflw.employee.domain.Employee;
import com.pplflw.employee.domain.EmployeeState;
import com.pplflw.employee.dto.CreateEmployeeRequest;
import com.pplflw.employee.dto.EmployeeEvent;
import com.pplflw.employee.dto.EmployeeResponse;
import com.pplflw.employee.dto.UpdateEmployeeStateRequest;
import com.pplflw.employee.exception.InvalidIdException;
import com.pplflw.employee.repository.EmployeeRepository;

@Service
public class EmployeeServiceImp implements EmployeeService {
	private final EmployeeRepository employeeRepository;
	private final StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;
	private final ModelMapper modelMapper;
	private final KafkaTemplate<String, Employee> kafkaTemplate;
	private final KafkaProperties kafkaProperties;

	public EmployeeServiceImp(EmployeeRepository employeeRepository,
			StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory, ModelMapper modelMapper,
			KafkaTemplate<String, Employee> kafkaTemplate,
			KafkaProperties kafkaProperties) {
		this.employeeRepository = employeeRepository;
		this.stateMachineFactory = stateMachineFactory;
		this.modelMapper = modelMapper;
		this.kafkaTemplate = kafkaTemplate;
		this.kafkaProperties = kafkaProperties;
	}

	@Override
	public List<EmployeeResponse> getAll() {
		List<Employee> employeeList = employeeRepository.findAll();

		List<EmployeeResponse> employeeResponseList = employeeList.stream()
				.map(employee -> modelMapper.map(employee, EmployeeResponse.class)).collect(Collectors.toList());
		return employeeResponseList;
	}

	@Override
	public EmployeeResponse getOne(long employeeId) throws InvalidIdException {
		Optional<Employee> employee = employeeRepository.findById(employeeId);
		if (employee.isEmpty()) {
			throw new InvalidIdException();
		}

		EmployeeResponse employeeResponse = modelMapper.map(employee.get(), EmployeeResponse.class);
		return employeeResponse;
	}

	@Override
	public EmployeeResponse create(CreateEmployeeRequest createEmployeeRequest) {
		Employee employee = modelMapper.map(createEmployeeRequest, Employee.class);
		employee.setState(EmployeeState.ADDED);
		employeeRepository.save(employee);

		kafkaTemplate.send(kafkaProperties.getEmployeeTopic(), employee);

		EmployeeResponse employeeResponse = modelMapper.map(employee, EmployeeResponse.class);
		return employeeResponse;
	}

	@Override
	public EmployeeResponse updateState(long employeeId, UpdateEmployeeStateRequest updateEmployeeStateRequest)
			throws InvalidIdException {
		Optional<Employee> employee = employeeRepository.findById(employeeId);
		if (employee.isEmpty()) {
			throw new InvalidIdException();
		}

		StateMachine<EmployeeState, EmployeeEvent> sm = stateMachineFactory
				.getStateMachine(Long.toString(employee.get().getId()));
		sm.stop();
		sm.getStateMachineAccessor().doWithAllRegions(sma -> {
			sma.resetStateMachine(new DefaultStateMachineContext<EmployeeState, EmployeeEvent>(
					employee.get().getState(), null, null, null));
		});
		sm.start();
		sm.sendEvent(updateEmployeeStateRequest.getEvent());

		employee.get().setState(sm.getState().getId());
		employeeRepository.save(employee.get());
		
		kafkaTemplate.send(kafkaProperties.getEmployeeTopic(), employee.get());

		EmployeeResponse employeeResponse = modelMapper.map(employee.get(), EmployeeResponse.class);
		return employeeResponse;
	}
}
