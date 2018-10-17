package nl.ou.testar.tgherkin.protocol;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import nl.ou.testar.GraphDB;
import nl.ou.testar.utils.report.Reporter;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

/**
 * Utility for reporting DocumentProtocol execution results.
 * Output can be generated to a text file and/or a database.
 * Data is collected during processing of the Testar protocol.
 *
 */
public class Report {

	/**
	 * Report separator.
	 */
	public static final String REPORT_SEPARATOR = "|";
	private static final String DOUBLE_QUOTE = "\"";
	
	private Report() {		
	}
	
	/**
	 * String report column.
	 *
	 */
	public enum StringColumn {
		/**
		 * Feature column.
		 */
		FEATURE,
		/**
		 * Scenario column.
		 */
		SCENARIO,
		/**
		 * Type column.
		 */
		TYPE,
		/**
		 * Step column.
		 */
		STEP,
		/**
		 * Selected action column.
		 */
		SELECTED_ACTION,
		/**
		 * Selected action details column.
		 */
		SELECTED_ACTION_DETAILS,
		/**
		 * Verdict column.
		 */
		VERDICT; 
	}

	/**
	 * Boolean report column.
	 *
	 */
	public enum BooleanColumn {
		/**
		 * Given column.
		 */
		GIVEN,
		/**
		 * Given mismatch column.
		 */
		GIVEN_MISMATCH,
		/**
		 * When column.
		 */
		WHEN_MISMATCH,
		/**
		 * Then column.
		 */
		THEN,
		/**
		 * Then mismatch column.
		 */
		THEN_MISMATCH;
	}
	
	/**
	 * Integer report column.
	 *
	 */
	public enum IntegerColumn {
		/**
		 * Sequence number column.
		 */
		SEQUENCE_NR,
		/**
		 * Action number column.
		 */
		ACTION_NR,
		/**
		 * Pre-generated derived actions column.
		 */
		PRE_GENERATED_DERIVED_ACTIONS,
		/**
		 * When derived actions column.
		 */
		WHEN_DERIVED_ACTIONS;
	}

	private static Map<Enum<?>,Object> reportMap = new LinkedHashMap<Enum<?>,Object>() {
		private static final long serialVersionUID = 3224898550792301262L;
	{
		put(IntegerColumn.SEQUENCE_NR, "SequenceNr");
		put(IntegerColumn.ACTION_NR,"ActionNr");
		put(StringColumn.FEATURE,"Feature");
		put(StringColumn.SCENARIO,"Scenario");
		put(StringColumn.TYPE,"Type");
		put(StringColumn.STEP,"Step");
		put(BooleanColumn.GIVEN,"Given");
		put(BooleanColumn.GIVEN_MISMATCH,"Given mismatch");
		put(IntegerColumn.PRE_GENERATED_DERIVED_ACTIONS,"Pre-generated derived actions");
		put(IntegerColumn.WHEN_DERIVED_ACTIONS,"When derived actions");
		put(BooleanColumn.WHEN_MISMATCH,"When mismatch");
		put(StringColumn.SELECTED_ACTION,"Selected action");
		put(StringColumn.SELECTED_ACTION_DETAILS,"Selected action details");
		put(BooleanColumn.THEN,"Then");
		put(BooleanColumn.THEN_MISMATCH,"Then mismatch");
		put(StringColumn.VERDICT,"Verdict");
	}};
	
	/**
     * Append report detail.
     * @param column identifier of a report column
     * @param value value of the report column 
     */
	public static void appendReportDetail(StringColumn column, String value) {
		reportMap.put(column, value);
	}

	/**
     * Append report detail.
     * @param column identifier of a report column
     * @param value value of the report column 
     */
	public static void appendReportDetail(BooleanColumn column, Boolean value) {
		reportMap.put(column, value);
	}

	/**
     * Append report detail.
     * @param column identifier of a report column
     * @param value value of the report column 
     */
	public static void appendReportDetail(IntegerColumn column, Integer value) {
		reportMap.put(column, value);
	}

	/**
	 * Report.
	 * @param state the SUT's current state
	 * @param action the action being reported on
	 * @param graphDB given database
	 * @param storeInTextFile indicator whether the report should be stored in a text file
	 * @param storeInDB indicator whether the report should be stored in a database
	 */
	public static void report(State state, Action action, GraphDB graphDB, boolean storeInTextFile, boolean storeInDB) {
		if (storeInTextFile) {
			StringBuilder reportDetail = new StringBuilder();
			boolean notFirst = false;
			for (Object value : reportMap.values()) {
				if (notFirst) {
					reportDetail.append(REPORT_SEPARATOR);
				} else {
					notFirst = true;
				}
				if (value != null) {
					reportDetail.append(transformReportValue(value.toString()));	
				}
			}
			reportDetail.append(System.getProperty("line.separator"));
			Reporter.getInstance().report(reportDetail.toString());
		}
		if (storeInDB) {
			if (action != null) {
				graphDB.addCustomType(action,"ReportedBy", getTgherkinInfo());
			} else {
				if (state != null) {
					// initial state: no action has been performed yet
					graphDB.addCustomType(state,"ReportedBy", getTgherkinInfo());
				}				
			}
		}
		// reset values
		for(Enum<?> key : reportMap.keySet()) {
			  reportMap.put(key, null);
		}
	}
	
	
	/**
	 * Transform report value to a value that is compatible with CSV. 
	 * @param value report value
	 * @return transformed value with corrections for usage of the report separator and line breaks
	 */
	public static String transformReportValue(String value) {
		// if value contains report separator: escape any double quotes and enclose value by double quotes 
		if (value.contains(REPORT_SEPARATOR)) {			
			value = value.replace(DOUBLE_QUOTE, DOUBLE_QUOTE + DOUBLE_QUOTE);
			value = DOUBLE_QUOTE + value + DOUBLE_QUOTE;
		}
		// replace line breaks by space
		value = value.replaceAll("\\r|\\n", "");
		value = value.replaceAll("\\r\\n|\\r|\\n", " ");
		return value;
		
	}

	private static TgherkinInfo getTgherkinInfo() {
		TgherkinInfo info = new TgherkinInfo(
				new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(System.currentTimeMillis())
				+ " " 
				+ reportMap.get(IntegerColumn.SEQUENCE_NR) + "-" + reportMap.get(IntegerColumn.ACTION_NR));
		// for now, do not pass null values to database
		info.set(TgherkinTags.TGHERKIN_SEQUENCE_NR,getTgherkinValue(IntegerColumn.SEQUENCE_NR));
		info.set(TgherkinTags.TGHERKIN_ACTION_NR,getTgherkinValue(IntegerColumn.ACTION_NR));
		info.set(TgherkinTags.TGHERKIN_FEATURE,getTgherkinValue(StringColumn.FEATURE));
		info.set(TgherkinTags.TGHERKIN_SCENARIO,getTgherkinValue(StringColumn.SCENARIO));
		info.set(TgherkinTags.TGHERKIN_TYPE,getTgherkinValue(StringColumn.TYPE));
		info.set(TgherkinTags.TGHERKIN_STEP,getTgherkinValue(StringColumn.STEP));
		info.set(TgherkinTags.TGHERKIN_GIVEN,getTgherkinValue(BooleanColumn.GIVEN));
		info.set(TgherkinTags.TGHERKIN_GIVEN_MISMATCH,getTgherkinValue(BooleanColumn.GIVEN_MISMATCH));
		info.set(TgherkinTags.TGHERKIN_PRE_GENERATED_DERIVED_ACTIONS,getTgherkinValue(IntegerColumn.PRE_GENERATED_DERIVED_ACTIONS));
		info.set(TgherkinTags.TGHERKIN_WHEN_DERIVED_ACTIONS,getTgherkinValue(IntegerColumn.WHEN_DERIVED_ACTIONS));
		info.set(TgherkinTags.TGHERKIN_WHEN_MISMATCH,getTgherkinValue(BooleanColumn.WHEN_MISMATCH));
		info.set(TgherkinTags.TGHERKIN_SELECTED_ACTION,getTgherkinValue(StringColumn.SELECTED_ACTION));
		info.set(TgherkinTags.TGHERKIN_SELECTED_ACTION_DETAILS,getTgherkinValue(StringColumn.SELECTED_ACTION_DETAILS));
		info.set(TgherkinTags.TGHERKIN_THEN,getTgherkinValue(BooleanColumn.THEN));
		info.set(TgherkinTags.TGHERKIN_THEN_MISMATCH,getTgherkinValue(BooleanColumn.THEN_MISMATCH));
		info.set(TgherkinTags.TGHERKIN_VERDICT,getTgherkinValue(StringColumn.VERDICT));
		return info;
	}
	
	private static String getTgherkinValue(StringColumn column) {
		String value = (String)reportMap.get(column);
		if (value == null) {
			return "";
		}	
		return value;
	}

	private static Boolean getTgherkinValue(BooleanColumn column) {
		Boolean value = (Boolean)reportMap.get(column);
		if (value == null) {
			return false;
		}	
		return value;
	}
	
	private static Integer getTgherkinValue(IntegerColumn column) {
		Integer value = (Integer)reportMap.get(column);
		if (value == null) {
			return 0;
		}	
		return value;
	}

}