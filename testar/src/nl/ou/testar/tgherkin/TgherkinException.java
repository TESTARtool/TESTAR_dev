package nl.ou.testar.tgherkin;

/**
 * Class that provides Tgherkin exceptions.
 *
 */
public class TgherkinException extends RuntimeException {

	private static final long serialVersionUID = -8685429379244196529L;
	
	/**
	 * Constructor.
	 * @param message exception message
	 */
	public TgherkinException(String message) { 
		this(message, null); 
	}
	
	/**
	 * Constructor.
	 * @param message exception message
	 * @param cause cause of the exception
	 */
	public TgherkinException(String message, Throwable cause) { 
		super(message, cause); 
	}
	
	/**
	 * Constructor.
	 * @param cause cause of the exception
	 */
	public TgherkinException(Throwable cause) { 
		super(cause); 
	}
	
	

}
