package nl.ou.testar.tgherkin;

/**
 * TgherkinException class.
 *
 */
public class TgherkinException extends RuntimeException {

	private static final long serialVersionUID = -8685429379244196529L;
	
	/**
	 * Constructor.
	 * @param message given message
	 */
	public TgherkinException(String message){ 
		this(message, null); 
	}
	
	/**
	 * Constructor.
	 * @param message given message
	 * @param cause given cause
	 */
	public TgherkinException(String message, Throwable cause){ 
		super(message, cause); 
	}
	
	/**
	 * Constructor.
	 * @param cause given cause
	 */
	public TgherkinException(Throwable cause){ 
		super(cause); 
	}
	
	

}
