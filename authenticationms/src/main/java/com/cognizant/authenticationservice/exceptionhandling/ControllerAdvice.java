package com.cognizant.authenticationservice.exceptionhandling;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cognizant.authenticationservice.errorhandling.ErrorMessage;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@RestControllerAdvice
public class ControllerAdvice {

	/**
	 * Exception : Username NOT FOUND
	 * 
	 * @param userNotFoundException
	 * @return
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessage userNotFoundException(UsernameNotFoundException userNotFoundException) {
		return new ErrorMessage(HttpStatus.FORBIDDEN, LocalDateTime.now(), userNotFoundException.getMessage());
	}

	/**
	 * Exception : Malformed JWT
	 * 
	 * @return
	 */
	@ExceptionHandler(MalformedJwtException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorMessage tokenMalformedException() {
		return new ErrorMessage(HttpStatus.UNAUTHORIZED, LocalDateTime.now(),
				"Not Authorized --> Token is malformed..");
	}

	/**
	 * Exception : Expired JWT
	 * 
	 * @return
	 */
	@ExceptionHandler({ ExpiredJwtException.class })
	public ResponseEntity<ErrorMessage> jwtExpired(ExpiredJwtException ex) {
		ErrorMessage err = new ErrorMessage();
		err.setStatus(HttpStatus.FORBIDDEN);
		err.setTimestamp(LocalDateTime.now());
		err.setMessage("JWT Expired");

		return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
	}
//	@ResponseStatus(HttpStatus.UNAUTHORIZED)
//	public ErrorMessage expiredTokenException() {
//		return new ErrorMessage(HttpStatus.UNAUTHORIZED, LocalDateTime.now(), "Not Authorized --> Token is Invalid..");
//	}

	/**
	 * Exception : Invalid signature
	 * 
	 * @return
	 */
	@ExceptionHandler(SignatureException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorMessage tokenSignatureException() {
		return new ErrorMessage(HttpStatus.UNAUTHORIZED, LocalDateTime.now(),
				"Not Authorized --> Token has invalid signature..");
	}

}