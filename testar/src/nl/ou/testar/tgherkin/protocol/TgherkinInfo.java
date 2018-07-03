package nl.ou.testar.tgherkin.protocol;

import nl.ou.testar.CustomType;

/**
 * TgherkinInfo represents a report of the execution results.
 *
 */
public class TgherkinInfo extends CustomType {
	private static final long serialVersionUID = -6248265781837738827L;
	private static final String TYPE = "TgherkinInfo";
	
	/**
	 * Constructor.
	 * @param title identifying description
	 */
	public TgherkinInfo(final String title) {
		super(TYPE,title);
	}
}
