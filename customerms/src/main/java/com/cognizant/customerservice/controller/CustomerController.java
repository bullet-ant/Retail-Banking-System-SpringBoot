package com.cognizant.customerservice.controller;

import java.net.BindException;
import java.time.DateTimeException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.customerservice.feign.AuthorizationFeign;
import com.cognizant.customerservice.model.CustomerEntity;
import com.cognizant.customerservice.service.CustomerService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Slf4j
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	AuthorizationFeign authorizationFeign;

	/**
	 * Creates a customer
	 * 
	 * @param token
	 * @param customer
	 * @param bindingResult
	 * @return
	 * @throws DateTimeException
	 * @throws BindException
	 */
	@PostMapping("/createCustomer")
	@ApiOperation(value = "Create a new customer", notes = "Create a new customer with the details provided")
	public ResponseEntity<?> createCustomer(
			@ApiParam(value = "Token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "Customer details for creating a customer", required = true) @Valid @RequestBody CustomerEntity customer,
			BindingResult bindingResult) throws DateTimeException, BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException();
		}

		customerService.hasEmployeePermission(token);

		CustomerEntity customerEntity = customerService.createCustomer(token, customer);

		if (customerEntity != null)
			return new ResponseEntity<>(customerEntity, HttpStatus.CREATED);
		else
			return new ResponseEntity<>("Customer Creation is UNSUCCESSFUL", HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * Save customer data into database
	 * 
	 * @param token
	 * @param customer
	 * @return
	 */
	@PostMapping("/saveCustomer")
	@ApiOperation(value = "Saves customer into the database", notes = "Saves a customer after getting the details ", response = CustomerEntity.class)
	public CustomerEntity saveCustomer(
			@ApiParam(value = "token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "customer details", required = true) @Valid @RequestBody CustomerEntity customer) {
		customerService.hasEmployeePermission(token);
		CustomerEntity customerEntity = customerService.saveCustomer(token, customer);
		if (customerEntity != null)
			return customerEntity;
		else
			return null;
	}

	/**
	 * Updates the customer in database
	 * 
	 * @param token
	 * @param customer
	 * @return
	 */
	@PutMapping("/updateCustomer")
	@ApiOperation(value = "Update Customer", notes = "Updates a customer after making the desired changes ", response = CustomerEntity.class)
	public CustomerEntity updateCustomer(
			@ApiParam(value = "token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "updated customer details", required = true) @Valid @RequestBody CustomerEntity customer) {
		customerService.hasEmployeePermission(token);
		return customerService.updateCustomer(token, customer);
	}

	/**
	 * Gets a customer details given it's userid
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@GetMapping("/getCustomerDetails/{id}")
	@ApiOperation(value = "Get customer details given their userid ", notes = "get the details of a specific customer ", response = CustomerEntity.class)
	public ResponseEntity<?> getCustomerDetails(
			@ApiParam(value = "token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "Id of customer whose detail is to be retrieved", required = true) @PathVariable String id) {
		customerService.hasPermission(token);
		CustomerEntity toReturnCustomerDetails = customerService.getCustomerDetail(token, id);
		if (toReturnCustomerDetails == null)
			return new ResponseEntity<>("Customer Userid " + id + " DOES NOT EXISTS", HttpStatus.NOT_ACCEPTABLE);
		toReturnCustomerDetails.setPassword(null);
		return new ResponseEntity<>(toReturnCustomerDetails, HttpStatus.OK);
	}

	/**
	 * Deletes a customer from database
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@DeleteMapping("deleteCustomer/{id}")
	@ApiOperation(value = "Delete customer given its userid", notes = "Deletes a specific customer ")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> deleteCustomer(
			@ApiParam(value = "token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "id of customer that is to be deleted", required = true) @PathVariable String id) {

		customerService.hasEmployeePermission(token);

		CustomerEntity checkCustomerIdExists = null;
		checkCustomerIdExists = customerService.getCustomerDetail(token, id);
		if (checkCustomerIdExists == null) {
			return new ResponseEntity<>("Customer Userid DOES NOT EXISTS", HttpStatus.NOT_ACCEPTABLE);
		}

		log.info("Starting deletion of --> {}", id);
		customerService.deleteCustomer(id);
		log.info("Deleted");
		return new ResponseEntity<>("Deleted SUCCESSFULLY", HttpStatus.OK);
	}

	@GetMapping("/getCustomers")
	public ResponseEntity<?> getCustomers(@RequestHeader("Authorization") String token) {
		customerService.hasPermission(token);
		List<CustomerEntity> customers = customerService.getCustomers(token);
		if (customers == null)
			return new ResponseEntity<>("No customers available", HttpStatus.NOT_ACCEPTABLE);
		return new ResponseEntity<>(customers, HttpStatus.OK);
	}

}
