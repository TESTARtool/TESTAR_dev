package nl.ou.testar.tgherkin.protocol;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fruit.Util;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.monkey.ConfigTags;

import nl.ou.testar.tgherkin.TgherkinImageFileAnalyzer;
import nl.ou.testar.tgherkin.Utils;
import nl.ou.testar.tgherkin.functions.Image;
import nl.ou.testar.tgherkin.functions.OCR;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.model.ProtocolProxy;
import nl.ou.testar.utils.report.ReportItem;

/**
 * Class responsible for the generation of the Tgherkin State report.
 *
 */
public class StateReportItem extends ReportItem {
  private static final String OUT_DIR = "output" + File.separator + "tgherkin" + File.separator;
  private static final String REPORT_NAME_PREFIX = "State_";
  private static final String REPORT_NAME_SUFFIX = ".csv";
  private static final String OCR_COLUMN_NAME = "OCR";
  private static final String IMAGE_RECOGNITION_COLUMN_PREFIX = "Image_";
  private static final String IMAGE_RECOGNITION_CONFIDENCE_COLUMN_SUFFIX = "_Confidence";
  private static final String PARENT = "#Parent";
  private static final String CHILD_COUNT = "$ChildCount";

  /**
   * Constructor.
   * @param append indicator whether data should be appended to the file
   * @param proxy document protocol proxy
   */
  public StateReportItem(boolean append, ProtocolProxy proxy) {
    super(OUT_DIR + "sequence" + proxy.getSequenceCount() +  File.separator +
        REPORT_NAME_PREFIX +
        proxy.getSequenceCount() + "_" + proxy.getActionCount() + "_" +
        new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(System.currentTimeMillis())
            + REPORT_NAME_SUFFIX, null, append);
    setData(reportState(proxy));
  }

  private static String reportState(ProtocolProxy proxy) {
    List<String> imageFiles = getImageFiles(proxy);
    SortedSet<String> header = new TreeSet<String>();
    List<SortedMap<String,String>> reportLines = new ArrayList<SortedMap<String,String>>();
    Iterator<Widget> iterator = proxy.getState().iterator();
    while (iterator.hasNext()) {
      Widget widget = iterator.next();
      SortedMap<String,String> reportLine = new TreeMap<String,String>();
          for (Tag<?> tag: widget.tags()) {
            String tagValue = Util.toString((Object)widget.get(tag));
            if (tagValue != null && !"".equals(tagValue)) {
              // only include non-empty values
              if (!header.contains(tag.name())) {
                header.add(tag.name());
              }
              reportLine.put(tag.name(), tagValue);
            }
          }
          // add parent and number of children for convenience
          String parentContractID = null;
          if (widget.parent() != null) {
            parentContractID = widget.parent().get(Tags.ConcreteID, null);
            if (parentContractID != null) {
              if (!header.contains(PARENT)) {
                header.add(PARENT);
              }
              reportLine.put(PARENT, parentContractID);
            }
          }
        if (!header.contains(CHILD_COUNT)) {
          header.add(CHILD_COUNT);
        }
          reportLine.put(CHILD_COUNT, "" + widget.childCount());
          includeOCR(proxy, widget, header, reportLine);
          includeImageRecognition(proxy, widget, imageFiles, header, reportLine);
          reportLines.add(reportLine);
    }
    return outputState(header, reportLines);
  }

  private static String outputState(SortedSet<String> header, List<SortedMap<String,String>> reportLines) {
    StringBuilder reportContent = new StringBuilder();
    // header
    boolean notFirst = false;
    Iterator<String> headerIterator = header.iterator();
    while (headerIterator.hasNext()) {
      String columnName = headerIterator.next();
      if (notFirst) {
        reportContent.append(Report.REPORT_SEPARATOR);
      } else {
        notFirst = true;
      }
      if (columnName.startsWith("#") || columnName.startsWith("$")) {
        // skip hash and dollar(used to get Parent and ChildCount in front of the widget tag columns)
        reportContent.append(Report.transformReportValue(columnName.substring(1)));
      } else {
        reportContent.append(Report.transformReportValue(columnName));
      }
    }
    reportContent.append(System.getProperty("line.separator"));
    // Data Lines
    for (SortedMap<String,String> reportLine: reportLines) {
      notFirst = false;
      headerIterator = header.iterator();
      while (headerIterator.hasNext()) {
        String columnName = headerIterator.next();
        if (notFirst) {
          reportContent.append(Report.REPORT_SEPARATOR);
        } else {
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

  private static List<String> getImageFiles(ProtocolProxy proxy) {
    if (proxy.getSettings().get(ConfigTags.TgherkinReportIncludeImageRecognition)) {
      TgherkinParser parser = Utils.getTgherkinParser(proxy.getTgherkinSourceCode());
      return new TgherkinImageFileAnalyzer().visitDocument(parser.document());
    } else {
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
      for (String imageFile: imageFiles) {
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
}
