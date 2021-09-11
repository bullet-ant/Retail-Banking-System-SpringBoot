package com.cognizant.transactionservice.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.transactionservice.feign.AccountFeign;
import com.cognizant.transactionservice.feign.RulesFeign;
import com.cognizant.transactionservice.models.Transaction;
import com.cognizant.transactionservice.repository.TransactionRepository;
import com.cognizant.transactionservice.service.TransactionServiceInterface;
import com.cognizant.transactionservice.util.AccountInput;
import com.cognizant.transactionservice.util.TransactionInput;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class TransactionRestController {

	public static final String METHOD_FOR_MAKETRANSFER = "AccountFallbackForTransfer";
	public static final String METHOD_FOR_MAKEWITHDRAW = "AccountFallbackForWithdraw";
	public static final String METHOD_FOR_MAKEDEPOSIT = "AccountFallbackForDeposit";

	@Autowired
	AccountFeign accountFeign;

	@Autowired
	RulesFeign rulesFeign;

	@Autowired
	TransactionRepository transRepo;

	@Autowired
	TransactionServiceInterface transactionService;

	/**
	 * MakeTransfer method Transfers amount from One account to another
	 */
	@PostMapping(value = "/transactions")
	@ApiOperation(value = "Transfer amount ", notes = "Transfers amount from source acc to target acc ")
	public boolean makeTransfer(
			@ApiParam(value = "token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "details required for transfer", required = true) @Valid @RequestBody TransactionInput transactionInput) {
		log.info("inside transaction method");
		log.info(transactionInput.toString());
		if (transactionInput != null) {
			boolean isComplete = transactionService.makeTransfer(token, transactionInput);

			return isComplete;
		} else {
			return false;
		}
	}

	/*
	 * Fallback method for transfer
	 */
	public boolean accountFallbackForTransfer() {
		log.error("Account Microservice is DOWN!");
		return false;
	}

	/**
	 * To get All transaction done in one account
	 * 
	 * @param token
	 * @param accId
	 * @return
	 */
	@ApiOperation(value = "Get all transactions", notes = "Provide the id of the customer whose all transactions is to be retrieved")
	@GetMapping(value = "/getAllTransByAccId/{id}")
	public List<Transaction> getTransactionsByAccId(
			@ApiParam(value = "token passed in header for authentication", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "id of customer ", required = true) @PathVariable("id") long accId) {
		List<Transaction> slist = transRepo.findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(accId, accId);
		return slist;
	}

	/**
	 * Method for making a withdraw in one account
	 * 
	 * @param token
	 * @param accountInput
	 * @return
	 */
	@PostMapping(value = "/withdraw")
	@ApiOperation(value = "Withdraw Amount ", notes = "To withdraw cash from the account")
	public boolean makeWithdraw(
			@ApiParam(value = "token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "Account Details", required = true) @Valid @RequestBody AccountInput accountInput) {
		transactionService.makeWithdraw(token, accountInput);
		return true;
	}

	@PostMapping(value = "/servicecharge")
	@ApiOperation(value = "Service Charge ", notes = "Service Charge to be cut for not maintaining the minimum balance ")
	public boolean makeServiceCharges(
			@ApiParam(value = "token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "Account Details", required = true) @Valid @RequestBody AccountInput accountInput) {
		transactionService.makeServiceCharges(token, accountInput);
		return true;
	}

	/*
	 * Fallback method for Withdraw
	 */
	public boolean accountFallbackForWithdraw() {
		log.error("Rules Microservice is DOWN!");
		return false;
	}

	/**
	 * Method for making a deposit in a account
	 * 
	 * @param token
	 * @param accountInput
	 * @return
	 */
	@PostMapping(value = "/deposit")
	@ApiOperation(value = "Deposit Amount ", notes = "To deposit cash in the account")
	public ResponseEntity<?> makeDeposit(
			@ApiParam(value = "token for authentication passed in header", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "Account details", required = true) @Valid @RequestBody AccountInput accountInput) {
		transactionService.makeDeposit(token, accountInput);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

	/*
	 * Fallback method for deposit
	 */
	public ResponseEntity<?> accountFallbackForDeposit() {
		log.error("Rules Microservice is DOWN!");
		return new ResponseEntity<>(false, HttpStatus.GATEWAY_TIMEOUT);
	}

}
