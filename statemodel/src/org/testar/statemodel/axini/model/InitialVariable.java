package org.testar.statemodel.axini.model;

/** 
 * AMP Initial variables.
 * 
 * stores the Axini model information of e.g.,
 *
 * ParaBank | Welcome
 * https://para.testar.org/parabank/index.htm
 * 
 * */
public class InitialVariable {
	private String url; // e.g., https://para.testar.org/parabank/
	private String page; // e.g., ParaBank | Welcome

	public InitialVariable() {}

	public InitialVariable(String url, String page) {
		this.url = url;
		this.page = page;
	}

	public String getUrl() {
		return url;
	}

	public String getPage() {
		return page;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPage(String page) {
		this.page = page;
	}

	// Debugging string representation, not used for rendering the AMP code
	@Override
	public String toString() {
		return "InitialVariable{" +
				"url='" + url + '\'' +
				", page='" + page + '\'' +
				'}';
	}
}
