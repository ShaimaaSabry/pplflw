package com.pplflw.employee.config;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.pplflw.employee.domain.EmployeeState;
import com.pplflw.employee.dto.EmployeeEvent;

@Configuration
@EnableStateMachineFactory
public class EmployeeStateMachineConfig extends StateMachineConfigurerAdapter<EmployeeState, EmployeeEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<EmployeeState, EmployeeEvent> states) throws Exception {
	states
		.withStates()
		.initial(EmployeeState.ADDED)
		.states(EnumSet.allOf(EmployeeState.class))
		.end(EmployeeState.ACTIVE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EmployeeState, EmployeeEvent> transitions) throws Exception {
	transitions
		.withExternal().source(EmployeeState.ADDED).target(EmployeeState.INCHECK).event(EmployeeEvent.CHECK)
		.and()
		.withExternal().source(EmployeeState.INCHECK).target(EmployeeState.APPROVED).event(EmployeeEvent.APPROVE)
		.and()
		.withExternal().source(EmployeeState.APPROVED).target(EmployeeState.ACTIVE).event(EmployeeEvent.ACTIVATE);
    }
}
