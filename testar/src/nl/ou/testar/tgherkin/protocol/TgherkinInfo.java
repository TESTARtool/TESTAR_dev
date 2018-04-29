package nl.ou.testar.tgherkin.protocol;

import nl.ou.testar.CustomType;

/**
 * The TgherkinInfo class is a custom type for reporting the execution results.
 *
 */
public class TgherkinInfo extends CustomType {
	private static final long serialVersionUID = -6248265781837738827L;
	private static final String TYPE = "TgherkinInfo";
	/**
	 * Constructor.
	 * @param title given title
	 */
	public TgherkinInfo(final String title) {
		super(TYPE,title);
	}
}
