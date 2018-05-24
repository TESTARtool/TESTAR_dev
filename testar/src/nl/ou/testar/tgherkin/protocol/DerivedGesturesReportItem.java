package nl.ou.testar.tgherkin.protocol;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fruit.Util;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Widget;

import nl.ou.testar.tgherkin.model.Gesture;
import nl.ou.testar.utils.report.ReportItem;

/**
 * Class responsible for the generation of the Tgherkin Derived Gestures report.
 *
 */
public class DerivedGesturesReportItem extends ReportItem {
	private static final String REPORT_NAME_PREFIX = "output" + File.separator + "DerivedGestures_";
	private static final String REPORT_NAME_SUFFIX = ".csv";
	private Map<Widget, List<Gesture>> gesturesMap;
	
	
	/**
	 * Constructor.
	 * @param append indicator whether data should be appended to the file
	 * @param sequenceCount sequence number
	 * @param actionCount action number
	 * @param gesturesMap map with widget as key and list of gestures as value
	 */
	public DerivedGesturesReportItem(boolean append, int sequenceCount, int actionCount, Map<Widget, List<Gesture>> gesturesMap) {
		super(REPORT_NAME_PREFIX + 
				sequenceCount + "_" + actionCount + "_" + 
				new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(System.currentTimeMillis()) + 			
				REPORT_NAME_SUFFIX,
				null,
				append);
		// postpone filling of data attribute: fill data from map in the process method that executed in a separate thread.
		this.gesturesMap = gesturesMap;
	}
	
	@Override
	public void process() {
		setData(reportDerivedGestures(gesturesMap));
		super.process();
	}
	
	private static String reportDerivedGestures(Map<Widget, List<Gesture>> map) {
		SortedSet<String> header = new TreeSet<String>();
		List<SortedMap<String,String>> reportLines = new ArrayList<SortedMap<String,String>>();
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
		return outputDerivedGestures(header, reportLines);
	}

	private static String outputDerivedGestures(SortedSet<String> header, List<SortedMap<String,String>> reportLines){
		StringBuilder reportContent = new StringBuilder();
		// header
		boolean notFirst = false;
		Iterator<String> headerIterator = header.iterator();
		while (headerIterator.hasNext()) {
			String columnName = headerIterator.next();
			if (notFirst) {
				reportContent.append(Report.REPORT_SEPARATOR);
			}else {
				notFirst = true;
			}
			if (columnName.startsWith("#")) {
				// skip hash (used to get gestures in front of the widget tag columns)
				reportContent.append(Report.transformReportValue(columnName.substring(1)));
			}else {
				reportContent.append(Report.transformReportValue(columnName));
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
					reportContent.append(Report.REPORT_SEPARATOR);
				}else {
					notFirst = true;
				}
				String value = reportLine.get(columnName);
				if (value != null) {
					reportContent.append(Report.transformReportValue(value));	
				}				
			}
			reportContent.append(System.getProperty("line.separator"));
		}
		
		//
		return reportContent.toString();
	}
	

}
