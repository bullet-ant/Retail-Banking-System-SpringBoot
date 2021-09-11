package com.rulesservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.rulesservice.exception.MinimumBalanceException;
import com.rulesservice.feign.AccountFeign;
import com.rulesservice.model.Account;
import com.rulesservice.model.AccountInput;
import com.rulesservice.model.RulesInput;
import com.rulesservice.service.RulesService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class RulesController {
/**
 * Rule Contoller
 */
	private static final String INVALID = "Send Valid Details.";
	@Autowired
	public RulesService rulesService;
	@Autowired
	AccountFeign accountFeign;

	/**
	 * Evaluates the balance in account
	 * @param account
	 * @return
	 * @throws MinimumBalanceException
	 */
	@PostMapping("/evaluateMinBal")
	@ApiOperation(value="Evaluates Balance" , notes="Check Balance in the account if minimum then throw Minimum Balance Exception")
	public ResponseEntity<?> evaluate(@ApiParam(value="Details of the account of the customer", required = true)@RequestBody RulesInput account)
			throws MinimumBalanceException {
		// Jwt token is checked
		// check the accountId is Not null
		if (account.getCurrentBalance()== 0) {
			throw new MinimumBalanceException(INVALID);
		} else {
			boolean status = rulesService.evaluate(account);

			return new ResponseEntity<Boolean>(status,HttpStatus.OK);
		}
	}
	/**
	 * Calculates service charge 
	 * @param token
	 * @return
	 */

	// Service charges calculation
	@PostMapping("/serviceCharges")
	@ApiOperation(value="Calculates service charge",notes="Calculates the service charge based on the balance in account")
	public ResponseEntity<?> serviceCharges(@ApiParam(value="token for authentication passed in header")@RequestHeader("Authorization") String token) {
		// Jwt token is checked
		rulesService.hasPermission(token);
		
//		accountFeign.servicecharge(token, new AccountInput(account.getAccountId(), detected));
		try {
			List<Account> body = accountFeign.getAllacc(token).getBody();
			for(Account acc:body) {
				double detected=acc.getCurrentBalance()/10;
				if(acc.getCurrentBalance()<1000 && (acc.getCurrentBalance()-detected)>0)
					accountFeign.servicecharge(token, new AccountInput(acc.getAccountId(),detected));
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ResponseEntity.ok(accountFeign.getAllacc(token).getBody());
	}

	// If Feign Microservice is not working this fallback method is executed
	public ResponseEntity<String> evalMinimumBalanceFallback(){//@RequestHeader("Authorization") String token@RequestBody RulesInput account)
	return new ResponseEntity<String>("Minimum balance criteria fail", HttpStatus.BAD_GATEWAY);
	}

}
