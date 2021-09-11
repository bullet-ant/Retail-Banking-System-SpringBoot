package com.cognizant.authenticationservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.authenticationservice.exceptionhandling.AppUserNotFoundException;
import com.cognizant.authenticationservice.model.AppUser;
import com.cognizant.authenticationservice.model.AuthenticationResponse;
import com.cognizant.authenticationservice.repository.UserRepository;
import com.cognizant.authenticationservice.service.LoginService;
import com.cognizant.authenticationservice.service.Validationservice;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for Authentication microservice
 *
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LoginService loginService;

	@Autowired
	private Validationservice validationService;

	/**
	 * The health method to check app
	 *
	 */
	@GetMapping("/health")
	@ApiOperation(value = "Checks the health of Authentication microservice")
	public ResponseEntity<String> healthCheckup() {
		log.info("Health Check for Authentication Microservice");
		log.info("health checkup ----->{}", "up");
		return new ResponseEntity<>("Up and running...", HttpStatus.OK);
	}

	/**
	 * Authenticate user based on given login credentials
	 * 
	 * @param appUserloginCredentials
	 * @return
	 * @throws UsernameNotFoundException
	 * @throws AppUserNotFoundException
	 */
	@PostMapping("/login")
	@ApiOperation(value = "Login user", notes = "In order to login the user has to provide its credentials")
	public ResponseEntity<AppUser> login(
			@ApiParam(value = "User login credentials", required = true) @RequestBody AppUser appUserloginCredentials)
			throws UsernameNotFoundException, AppUserNotFoundException {
		AppUser user = loginService.userLogin(appUserloginCredentials);
		log.info("Credentials ----->{}", user);
		return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
	}

	/**
	 * Checks validity of the token
	 * 
	 * @param token
	 * @return
	 */
	@GetMapping("/validateToken")
	@ApiOperation(value = "Validate token", notes = "Validates token ", response = AuthenticationResponse.class)
	public AuthenticationResponse getValidity(
			@ApiParam(value = "Token for validation", required = true) @RequestHeader("Authorization") final String token) {
		log.info("Token Validation ----->{}", token);
		return validationService.validate(token);
	}

	/**
	 * Creates a new user in UserRepository
	 * 
	 * @param appUserCredentials
	 * @return
	 */
	@PostMapping("/createUser")
	@ApiOperation(value = "Create a new user", notes = "Creates user by providing valid login credentials")
	public ResponseEntity<?> createUser(
			@ApiParam(value = "User credentials", required = true) @RequestBody AppUser appUserCredentials) {
		AppUser createduser = null;
		try {
			createduser = userRepository.save(appUserCredentials);
		} catch (Exception e) {
			return new ResponseEntity<String>("Not created", HttpStatus.NOT_ACCEPTABLE);
		}
		log.info("user creation---->{}", createduser);
		return new ResponseEntity<>(createduser, HttpStatus.CREATED);

	}

	/**
	 * Get all users in DB (Accessible only by user with role EMPLOYEE)
	 * 
	 * @param token
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
	@GetMapping("/find")
	@ApiOperation(value = "Get all users", notes = "EMPLOYEE role required for this operation")
	public ResponseEntity<List<AppUser>> findUsers(
			@ApiParam(value = "Token for authentication passed in header", required = true) @RequestHeader("Authorization") final String token) {
		List<AppUser> createduser = new ArrayList<>();
		List<AppUser> findAll = userRepository.findAll();
		findAll.forEach(emp -> createduser.add(emp));
		log.info("All Users  ----->{}", findAll);
		return new ResponseEntity<>(createduser, HttpStatus.CREATED);

	}

	/**
	 * Get role of the user based on userid
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/role/{id}")
	@ApiOperation(value = "Get role", notes = "Pass id of the user whose role is to be retrieved")
	public String getRole(@ApiParam(value = "id of user", required = true) @PathVariable("id") String id) {
		return userRepository.findById(id).get().getRole();
	}

}