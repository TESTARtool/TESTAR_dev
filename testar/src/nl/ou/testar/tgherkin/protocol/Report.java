package nl.ou.testar.tgherkin.protocol;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Widget;
import org.fruit.monkey.ConfigTags;

import nl.ou.testar.GraphDB;
import nl.ou.testar.tgherkin.TgherkinImageFileAnalyzer;
import nl.ou.testar.tgherkin.Utils;
import nl.ou.testar.tgherkin.functions.Image;
import nl.ou.testar.tgherkin.functions.OCR;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.model.Gesture;
import nl.ou.testar.tgherkin.model.ProtocolProxy;
import nl.ou.testar.utils.report.Reporter;

/**
 * Report on DocumentProtocol execution.
 * Output can be generated to a text file and/or a database.
 *
 */
public class Report {

	private static final String DOUBLE_QUOTE = "\"";
	private static final String OCR_COLUMN_NAME = "OCR";
	private static final String IMAGE_RECOGNITION_COLUMN_PREFIX = "Image_";
	private static final String IMAGE_RECOGNITION_CONFIDENCE_COLUMN_SUFFIX = "_Confidence";
	
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

	/**
	 * Report separator.
	 */
	public static final String REPORT_SEPARATOR = "|";
	private static Map<Enum<?>,Object> reportMap = new LinkedHashMap<Enum<?>,Object>(){
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
     * @param column given column name
     * @param value given column value 
     */
	public static void appendReportDetail(StringColumn column, String value) {
		reportMap.put(column, value);
	}

	/**
     * Append report detail.
     * @param column given column name
     * @param value given column value 
     */
	public static void appendReportDetail(BooleanColumn column, Boolean value) {
		reportMap.put(column, value);
	}

	/**
     * Append report detail.
     * @param column given column name
     * @param value given column value 
     */
	public static void appendReportDetail(IntegerColumn column, Integer value) {
		reportMap.put(column, value);
	}

	/**
	 * Report.
	 * @param state given state
	 * @param action given action
	 * @param graphDB given database
	 * @param storeInTextFile indicator store in text file
	 * @param storeInDB indicator store in database
	 */
	public static void report(State state, Action action, GraphDB graphDB, boolean storeInTextFile, boolean storeInDB){
		if (storeInTextFile) {
			StringBuilder reportDetail = new StringBuilder();
			boolean notFirst = false;
			for (Object value : reportMap.values()) {
				if (notFirst) {
					reportDetail.append(REPORT_SEPARATOR);
				}else {
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
			}else {
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
	 * Report derived gestures.
	 * @param proxy given document protocol proxy
	 * @param map given widget-gesture list map
	 */
	public static void reportDerivedGestures(ProtocolProxy proxy, Map<Widget, List<Gesture>> map) {
		SortedSet<String> header = new TreeSet<String>();
		List<SortedMap<String,String>> reportLines = new ArrayList<SortedMap<String,String>>();
		String reportName = "output" + File.separator + "DerivedGestures_" + 
				proxy.getSequenceCount() + "_" + proxy.getActionCount() + "_" + 
				new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(System.currentTimeMillis()) + ".csv";
		Iterator<Map.Entry<Widget,List<Gesture>>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Widget,List<Gesture>> entrySet = iterator.next();
			Widget widget = entrySet.getKey();
			List<Gesture> gestureList = entrySet.getValue();
			SortedMap<String,String> reportLine = new TreeMap<String,String>();
	        for (Tag<?> tag : widget.tags()) {
	        	String tagValue = Util.toString((Object)widget.get(tag));
	        	if (tagValue != null && !"".equals(tagValue)) {
	        		// only include non-empty values
	        		if (!header.contains(tag.name())) {
	        			header.add(tag.name());	        			
	        		}
	        		reportLine.put(tag.name(), tagValue);
	        	}
	        }        
			for (Gesture gesture : gestureList) {
				String gestureName = "#" + gesture.getClass().getSimpleName();
        		if (!header.contains(gestureName)) {
        			header.add(gestureName);	        			
        		}
        		reportLine.put(gestureName, "Yes");
			}
			reportLines.add(reportLine);
		}
		outputDerivedGestures(header, reportLines, reportName);
	}

	private static void outputDerivedGestures(SortedSet<String> header, List<SortedMap<String,String>> reportLines, String reportName){
		StringBuilder reportContent = new StringBuilder();
		// header
		boolean notFirst = false;
		Iterator<String> headerIterator = header.iterator();
		while (headerIterator.hasNext()) {
			String columnName = headerIterator.next();
			if (notFirst) {
				reportContent.append(REPORT_SEPARATOR);
			}else {
				notFirst = true;
			}
			if (columnName.startsWith("#")) {
				// skip underscore (used to get gestures in front of the widget tag columns)
				reportContent.append(transformReportValue(columnName.substring(1)));
			}else {
				reportContent.append(transformReportValue(columnName));
			}
		}
		reportContent.append(System.getProperty("line.separator"));
		// Data Lines
		for (SortedMap<String,String> reportLine : reportLines) {
			notFirst = false;
			headerIterator = header.iterator();
			while (headerIterator.hasNext()) {
				String columnName = headerIterator.next();
				if (notFirst) {
					reportContent.append(REPORT_SEPARATOR);
				}else {
					notFirst = true;
				}
				String value = reportLine.get(columnName);
				if (value != null) {
					reportContent.append(transformReportValue(value));	
				}				
			}
			reportContent.append(System.getProperty("line.separator"));
		}
		
		//
		Reporter.getInstance().report(reportName, reportContent.toString());
	}
	
	/**
	 * Report state.
	 * @param proxy given document protocol proxy
	 */
	public static void reportState(ProtocolProxy proxy) {
		List<String> imageFiles = getImageFiles(proxy);
		SortedSet<String> header = new TreeSet<String>();
		List<SortedMap<String,String>> reportLines = new ArrayList<SortedMap<String,String>>();
		String reportName = "output" + File.separator + "State_" + 
				proxy.getSequenceCount() + "_" + proxy.getActionCount() + "_" + 
				new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(System.currentTimeMillis()) + ".csv";
		Iterator<Widget> iterator = proxy.getState().iterator();
		while (iterator.hasNext()) {
			Widget widget = iterator.next();
			SortedMap<String,String> reportLine = new TreeMap<String,String>();
	        for (Tag<?> tag : widget.tags()) {
	        	String tagValue = Util.toString((Object)widget.get(tag));
	        	if (tagValue != null && !"".equals(tagValue)) {
	        		// only include non-empty values
	        		if (!header.contains(tag.name())) {
	        			header.add(tag.name());	        			
	        		}
	        		reportLine.put(tag.name(), tagValue);
	        	}
	        }
	        includeOCR(proxy, widget, header, reportLine);
	        includeImageRecognition(proxy, widget, imageFiles, header, reportLine);
	        reportLines.add(reportLine);
		}
		outputState(header, reportLines, reportName);
	}

	private static List<String> getImageFiles(ProtocolProxy proxy) {
		if (proxy.getSettings().get(ConfigTags.TgherkinReportIncludeImageRecognition)) {
			TgherkinParser parser = Utils.getTgherkinParser(proxy.getTgherkinSourceCode());
			return new TgherkinImageFileAnalyzer().visitDocument(parser.document());
		}else {
			return new ArrayList<String>(); 
		}
		
	}

	private static void includeOCR(ProtocolProxy proxy, Widget widget, SortedSet<String> header, SortedMap<String,String> reportLine) {
		if (proxy.getSettings().get(ConfigTags.TgherkinReportIncludeOCR)) {
	        String ocrValue = OCR.getInstance().getOCR(proxy, widget);
			if (ocrValue != null && !"".equals(ocrValue)) {
		        if (!header.contains(OCR_COLUMN_NAME)) {
	    			header.add(OCR_COLUMN_NAME);	        			
	    		}
	    		reportLine.put(OCR_COLUMN_NAME, ocrValue);
			}
		}
	}
	
	private static void includeImageRecognition(ProtocolProxy proxy, Widget widget, List<String> imageFiles, SortedSet<String> header, SortedMap<String,String> reportLine) {
		if (proxy.getSettings().get(ConfigTags.TgherkinReportIncludeImageRecognition)) {
			for (String imageFile : imageFiles) {
				Boolean recognized = Image.getInstance().isRecognized(proxy, widget, imageFile);
		        String headerName = IMAGE_RECOGNITION_COLUMN_PREFIX + imageFile;
				if (!header.contains(headerName)) {
	    			header.add(headerName);	        			
	    		}
	    		reportLine.put(headerName, recognized.toString());
				Double confidence = Image.getInstance().getRecognitionConfidence(proxy, widget, imageFile);
		        headerName = IMAGE_RECOGNITION_COLUMN_PREFIX + imageFile + IMAGE_RECOGNITION_CONFIDENCE_COLUMN_SUFFIX;
				if (!header.contains(headerName)) {
	    			header.add(headerName);	        			
	    		}
	    		reportLine.put(headerName, String.format("%.2f", confidence));
			}
		}
	}
	
	private static void outputState(SortedSet<String> header, List<SortedMap<String,String>> reportLines, String reportName){
		StringBuilder reportContent = new StringBuilder();
		// header
		boolean notFirst = false;
		Iterator<String> headerIterator = header.iterator();
		while (headerIterator.hasNext()) {
			String columnName = headerIterator.next();
			if (notFirst) {
				reportContent.append(REPORT_SEPARATOR);
			}else {
				notFirst = true;
			}
			reportContent.append(transformReportValue(columnName));
		}
		reportContent.append(System.getProperty("line.separator"));
		// Data Lines
		for (SortedMap<String,String> reportLine : reportLines) {
			notFirst = false;
			headerIterator = header.iterator();
			while (headerIterator.hasNext()) {
				String columnName = headerIterator.next();
				if (notFirst) {
					reportContent.append(REPORT_SEPARATOR);
				}else {
					notFirst = true;
				}
				String value = reportLine.get(columnName);
				if (value != null) {
					reportContent.append(transformReportValue(value));	
				}				
			}
			reportContent.append(System.getProperty("line.separator"));
		}
		//
		Reporter.getInstance().report(reportName, reportContent.toString());
	}	
	
	private static String transformReportValue(String value){
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
				new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(System.currentTimeMillis()) +
				" " + reportMap.get(IntegerColumn.SEQUENCE_NR) + "-" + reportMap.get(IntegerColumn.ACTION_NR));
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