package com.cognizant.CustomerService.controller;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.cognizant.customerservice.CustomerServiceApplication;
import com.cognizant.customerservice.controller.CustomerController;
import com.cognizant.customerservice.exception.CustomerAlreadyExistException;
import com.cognizant.customerservice.model.AppUser;
import com.cognizant.customerservice.model.CustomerEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CustomerServiceApplication.class })
 public class CustomerTests {

	public String token ="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyNjc2OTcwOCwiaWF0IjoxNjI2NzY3OTA4fQ.0atPxpZptEdOTY3h5ToVdUtU1rnC9RRnQqxsA-R-PD8";
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wc;
	
	@Autowired
	CustomerController controller;

	List<AppUser> employees = new ArrayList<AppUser>();
	static ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() throws JsonProcessingException, Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wc).build();
	}

	@Test
	public void saveCustomer() throws JsonProcessingException, Exception {
		System.err.println(token);
		CustomerEntity menu = null;
		String json = mapper.writeValueAsString(menu);
		MvcResult andReturn = mockMvc
				.perform(MockMvcRequestBuilders.post("/createCustomer").header("Authorization", "Bearer " + token)
						.content(json).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(400)).andReturn();

	}
	
	@Test
	public void saveCustomers() throws JsonProcessingException, Exception {
		System.err.println(token);
		CustomerEntity menu = new CustomerEntity();
		menu.setAddress("Ramgarh");
		menu.setDateOfBirth(new Date(60));
		menu.setPan("HGFTY12324");
		menu.setPassword("password");
		menu.setUsername("Aman Kumar");
		menu.setUserid("aman");
		String json = mapper.writeValueAsString(menu);
		MvcResult andReturn = mockMvc
				.perform(MockMvcRequestBuilders.post("/saveCustomer").header("Authorization", "Bearer " + token)
						.content(json).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200)).andReturn();

	}
	
	@Test
	public void updateCustomers() throws JsonProcessingException, Exception {
		System.err.println(token);
		CustomerEntity menu = new CustomerEntity();
		menu.setAddress("Ramgarh");
		menu.setDateOfBirth(new Date(60));
		menu.setPan("HGFTY12324");
		menu.setPassword("password");
		menu.setUsername("Aman Kumar");
		menu.setUserid("aman");
		String json = mapper.writeValueAsString(menu);
		MvcResult andReturn = mockMvc
				.perform(MockMvcRequestBuilders.post("/updateCustomer").header("Authorization", "Bearer " + token)
						.content(json).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200)).andReturn();

	}

	@Test
	public void unsuccesfulCustomer() throws JsonProcessingException, Exception {
		System.err.println(token);
		CustomerEntity menu = new CustomerEntity();
		menu.setAddress("Ramgarh");
		menu.setDateOfBirth(new Date(60));
		menu.setPan("HGFTY12324");
		menu.setPassword("password");
		menu.setUsername("Aman Kumar");
		menu.setUserid("aman");
		String json = mapper.writeValueAsString(menu);
		MvcResult andReturn = mockMvc
				.perform(MockMvcRequestBuilders.post("/createCustomer").header("Authorization", "Bearer " + token)
						.content(json).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406)).andReturn();
	}

	

	@Test
	public void getOneEmployees() throws JsonProcessingException, Exception {
		System.err.println(token);
		mockMvc.perform(MockMvcRequestBuilders.get("/getCustomerDetails/aman")
				.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200));

	}

	@Test
	public void deleteEmployeeAPI() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/deleteCustomer/aman", 1).header("Authorization",
				"Bearer " + token)).andExpect(status().is(200));
	}

	@Test
	public void deleteNotPresentEmployeeAPI() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/deleteCustomer/amannn", 1).header("Authorization",
				"Bearer " + token)).andExpect(status().is(406));
	}

	@Test
	public void customerAlreadyExist() {
		CustomerAlreadyExistException e1 = new CustomerAlreadyExistException("aman");
		CustomerAlreadyExistException e2 = new CustomerAlreadyExistException("aman");
		assertThat(e1).isNotEqualTo(e2);
	}
	
}