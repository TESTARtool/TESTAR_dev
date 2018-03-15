package nl.ou.testar.tgherkin.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.ou.testar.utils.report.Reporter;

/**
 * Report on DocumentProtocol execution.
 *
 */
public class Report {

	/**
	 * Report column.
	 *
	 */
	public enum Column {
		SEQUENCE_NR,
		ACTION_NR,
		FEATURE, 
		SCENARIO, 
		TYPE,
		STEP, 
		GIVEN, 
		GIVEN_MISMATCH, 
		PRE_GENERATED_DERIVED_ACTIONS, 
		WHEN_DERIVED_ACTIONS, 
		WHEN_MISMATCH, 
		SELECTED_ACTION, 
		SELECTED_ACTION_DETAILS, 
		THEN, 
		THEN_MISMATCH, 
		VERDICT
	}

	/**
	 * Report separator.
	 */
	public static final String REPORT_SEPARATOR = "|";
	private static Map<Column,String> reportMap = new LinkedHashMap<Column,String>(){
		private static final long serialVersionUID = 3224898550792301262L;
	{
		put(Column.SEQUENCE_NR, "SequenceNr");
		put(Column.ACTION_NR,"ActionNr");
		put(Column.FEATURE,"Feature");
		put(Column.SCENARIO,"Scenario");
		put(Column.TYPE,"Type");
		put(Column.STEP,"Step");
		put(Column.GIVEN,"Given");
		put(Column.GIVEN_MISMATCH,"Given mismatch");
		put(Column.PRE_GENERATED_DERIVED_ACTIONS,"Pre-generated derived actions");
		put(Column.WHEN_DERIVED_ACTIONS,"When derived actions");
		put(Column.WHEN_MISMATCH,"When mismatch");
		put(Column.SELECTED_ACTION,"Selected action");
		put(Column.SELECTED_ACTION_DETAILS,"Selected action details");
		put(Column.THEN,"Then");
		put(Column.THEN_MISMATCH,"Then mismatch");
		put(Column.VERDICT,"Verdict");
	}};
	
	/**
     * Append report detail.
     * @param column given column name
     * @param value given column value 
     */
	public static void appendReportDetail(Column column, String value) {
		reportMap.put(column, value);
	}

	/**
	 * Report.
	 */
	public static void report(){
		StringBuilder reportDetail = new StringBuilder();
		boolean notFirst = false;
		for (String value : reportMap.values()) {
			if (notFirst) {
				reportDetail.append(REPORT_SEPARATOR);
			}else {
				notFirst = true;
			}
			reportDetail.append(value);
		}
		reportDetail.append(System.getProperty("line.separator"));
		Reporter.getInstance().report(reportDetail.toString());
		// reset values
		for(Column key : reportMap.keySet()) {
			  reportMap.put(key, "");
		}
	}
	
}
