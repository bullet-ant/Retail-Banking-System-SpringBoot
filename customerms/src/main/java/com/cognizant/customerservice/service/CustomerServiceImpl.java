package com.cognizant.customerservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.customerservice.exception.AccessDeniedException;
import com.cognizant.customerservice.exception.CustomerAlreadyExistException;
import com.cognizant.customerservice.feign.AccountFeign;
import com.cognizant.customerservice.feign.AuthorizationFeign;
import com.cognizant.customerservice.model.Account;
import com.cognizant.customerservice.model.AppUser;
import com.cognizant.customerservice.model.AuthenticationResponse;
import com.cognizant.customerservice.model.CustomerEntity;
import com.cognizant.customerservice.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

	private static final String CUSTOMER = "CUSTOMER";

	@Autowired
	AuthorizationFeign authorizationFeign;

	@Autowired
	AccountFeign accountFeign;

	@Autowired
	CustomerRepository customerRepo;

	/**
	 * Checks if token is valid
	 */
	@Override
	public AuthenticationResponse hasPermission(String token) {
		return authorizationFeign.getValidity(token);
	}

	/**
	 * Check is the user is an EMPLOYEE
	 */
	@Override
	public AuthenticationResponse hasEmployeePermission(String token) {
		AuthenticationResponse validity = authorizationFeign.getValidity(token);
		if (!authorizationFeign.getRole(validity.getUserid()).equals("EMPLOYEE"))
			throw new AccessDeniedException("NOT ALLOWED");
		else
			return validity;
	}

	/**
	 * Check if the user is a CUSTOMER
	 */
	@Override
	public AuthenticationResponse hasCustomerPermission(String token) {

		AuthenticationResponse validity = authorizationFeign.getValidity(token);

		if (!authorizationFeign.getRole(validity.getUserid()).equals(CUSTOMER))
			throw new AccessDeniedException("NOT ALLOWED");
		else
			return validity;
	}

	/**
	 * Creates a new customer
	 */
	@Override
	public CustomerEntity createCustomer(String token, CustomerEntity customer) {

		CustomerEntity checkCustomerExists = getCustomerDetail(token, customer.getUserid());

		// Checks if customer already exists
		if (checkCustomerExists != null) {
			throw new CustomerAlreadyExistException("Customer already exist");
		} else {
			AppUser user = new AppUser(customer.getUserid(), customer.getUsername(), customer.getPassword(), null,
					CUSTOMER);
			authorizationFeign.createUser(user);
		}

		for (Account acc : customer.getAccounts()) {
			accountFeign.createAccount(token, customer.getUserid(), acc);
		}

		customerRepo.save(customer);
		log.info("Consumer details saved.");
		return customer;
	}

	/**
	 * Get CustomerEntity from repository
	 */
	@Override
	public CustomerEntity getCustomerDetail(String token, String id) {
		Optional<CustomerEntity> customer = customerRepo.findById(id);

		if (!customer.isPresent())
			return null;

		log.info("Consumer details fetched --> ", customer.get().toString());

		List<Account> list = accountFeign.getCustomerAccount(token, id);

		customer.get().setAccounts(list);

		return customer.get();
	}

	/**
	 * Deletes a customer, provided his ID is given
	 */
	@Override
	public boolean deleteCustomer(String id) {
//		CustomerEntity customer = customerRepo.findById(id).get();
//
//		if (customer != null)
//			customerRepo.deleteById(id);
//		else
//			return false;

		Optional<CustomerEntity> optional = customerRepo.findById(id);
		if (optional.isPresent()) {
			customerRepo.deleteById(id);
		} else {
			return false;
		}

		log.info("Consumer details deleted.");

		return true;
	}

	/**
	 * Save customer into Customer Repository
	 */
	@Override
	public CustomerEntity saveCustomer(String token, CustomerEntity customer) {
		CustomerEntity checkCustomerExists = getCustomerDetail(token, customer.getUserid());

		if (checkCustomerExists == null) {
			AppUser user = new AppUser(customer.getUserid(), customer.getUsername(), customer.getPassword(), null,
					CUSTOMER);
			authorizationFeign.createUser(user);
		}

		return customerRepo.save(customer);
	}

	/**
	 * Updates customer details in Customer Repository
	 */
	@Override
	public CustomerEntity updateCustomer(String token, CustomerEntity customer) {
//		CustomerEntity toUpdate = customerRepo.findById(customer.getUserid()).get();
//
//		toUpdate.setAccounts(customer.getAccounts());
//
//		for (Account acc : customer.getAccounts()) {
//			accountFeign.createAccount(token, customer.getUserid(), acc);
//		}
//
//		return customerRepo.save(toUpdate);

		Optional<CustomerEntity> optional = customerRepo.findById(customer.getUserid());
		CustomerEntity toUpdate = new CustomerEntity();

		if (optional.isPresent()) {

			toUpdate = optional.get();

			toUpdate.setAccounts(customer.getAccounts());
		}

		for (Account acc : customer.getAccounts()) {
			accountFeign.createAccount(token, customer.getUserid(), acc);
		}

		return customerRepo.save(toUpdate);
	}

	/*
	 * service method to get all customers
	 */
	@Override
	public List<CustomerEntity> getCustomers(String token) {
		Iterable<CustomerEntity> customerList = customerRepo.findAll();
//		for (CustomerEntity acc : customerList) {
//			acc.setTransactions(transactionFeign.getTransactionsByAccId(token, acc.getAccountId()));
//		}
		return (List<CustomerEntity>) customerList;
	}

}
