package com.hotmart.ex.errorhandling;

/**
 * Exceção de parâmetro obrigatório.
 * 
 * @author Tays
 *
 */
public class RequiredParameterException extends RuntimeException {
	private static final long serialVersionUID = -2883280213277321432L;

	public RequiredParameterException(String message) {
		super(message);
	}
}
