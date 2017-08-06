package es.upv.staq.testar;

/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This software is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

public class UnsupportedPlatformException extends RuntimeException {

	private static final long serialVersionUID = -763558210570361617L;

	public UnsupportedPlatformException() {
		super();
	}

	public UnsupportedPlatformException(String message) {
		super(message);
	}

	public UnsupportedPlatformException(Throwable cause) {
		super(cause);
	}

	public UnsupportedPlatformException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedPlatformException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
