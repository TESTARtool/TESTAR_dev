package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.DslOracle;

public class TBuis_oracles {
   
   public static class ParticipantsButtonMustBeAToneOfBlue$229 extends DslOracle {
     /*
      assert for all button 
        it.backgroundColorName contains "blue" 
        when it.text contains "Participants" 
        "Participants button must be a tone of blue".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Participants button must be a tone of blue";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("button", state)) {
          
          Object widget$193$7 = getProperty($it, "text");
          boolean cond$201 = evaluateContains(widget$193$7, "Participants");
          if (cond$201) {
            
            Object widget$145$22 = getProperty($it, "backgroundColorName");
            boolean cond$168 = evaluateContains(widget$145$22, "blue");
            if (!cond$168) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class StudentViewTitleMustContainStudentSViewText$485 extends DslOracle {
     /*
      assert static_text "stu.view.title".text contains "Student's View" "Student view title must contain Student's View text".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Student view title must contain Student's View text";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$425$28 = getWidget("static_text", "stu.view.title", state);
        if (widget$425$28 == null) {
          return Verdict.OK;
        }
        Object widget$425$33 = getProperty(widget$425$28, "text");
        boolean cond$459 = evaluateContains(widget$425$33, "Student's View");
        if (!cond$459) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$425$28)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class TotalCreditsTextMustNotContainTwentyCredits$1230 extends DslOracle {
     /*
      assert static_text "Total Credits".text is equal to "Total Credits 0" 
        when table "mySubjects.completedTable".rows.length is equal to 0 "Total credits text must not contain twenty credits".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Total credits text must not contain twenty credits";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1170$33 = getWidget("table", "mySubjects.completedTable", state);
        if (widget$1170$33 == null) {
          return Verdict.OK;
        }
        Object widget$1170$38 = getProperty(widget$1170$33, "rows");
        Object widget$1170$45 = getProperty(widget$1170$38, "length");
        boolean cond$1216 = evaluateIsEqualTo(widget$1170$45, 0);
        if (cond$1216) {
          Widget widget$1098$27 = getWidget("static_text", "Total Credits", state);
          if (widget$1098$27 == null) {
            return Verdict.OK;
          }
          Object widget$1098$32 = getProperty(widget$1098$27, "text");
          boolean cond$1131 = evaluateIsEqualTo(widget$1098$32, "Total Credits 0");
          if (!cond$1131) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1098$27)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class MySubjectsLinkMustBeSelectedWithAToneOfBlueWhenSubjectSuccessfullyRemoveTextIsVisible$1603 extends DslOracle {
     /*
      assert link "My Subjects".backgroundColorName contains "blue" 
        when static_text "Subject has been successfully removed!" is visible 
        "My Subjects link must be selected with a tone of blue when subject successfully remove text is visible".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "My Subjects link must be selected with a tone of blue when subject successfully remove text is visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1535$52 = getWidget("static_text", "Subject has been successfully removed!", state);
        if (widget$1535$52 == null) {
          return Verdict.OK;
        }
        boolean cond$1588 = evaluateIsStatus(widget$1535$52, "visible");
        if (cond$1588) {
          Widget widget$1471$18 = getWidget("link", "My Subjects", state);
          if (widget$1471$18 == null) {
            return Verdict.OK;
          }
          Object widget$1471$38 = getProperty(widget$1471$18, "backgroundColorName");
          boolean cond$1510 = evaluateContains(widget$1471$38, "blue");
          if (!cond$1510) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1471$18)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class TaughtSubjectsTableMustContainTheSixCorrectColumnsInOrder$2243 extends DslOracle {
     /*
      assert table "mySubjects.table".headerText contains "# Subject Teacher(s) New exam date List of students Remove subject" "Taught Subjects table must contain the six correct columns in order".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Taught Subjects table must contain the six correct columns in order";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2129$24 = getWidget("table", "mySubjects.table", state);
        if (widget$2129$24 == null) {
          return Verdict.OK;
        }
        Object widget$2129$35 = getProperty(widget$2129$24, "headerText");
        boolean cond$2165 = evaluateContains(widget$2129$35, "# Subject Teacher(s) New exam date List of students Remove subject");
        if (!cond$2165) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2129$24)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class TableDataMustNeverContainTheNonameText$2672 extends DslOracle {
     /*
      assert for all table_data it.text not contains "Noname" "Table data must never contain the Noname text".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Table data must never contain the Noname text";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_data", state)) {
          
          Object widget$2642$7 = getProperty($it, "text");
          boolean cond$2650 = !(evaluateContains(widget$2642$7, "Noname"));
          if (!cond$2650) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class GradeDropdownMustContainALetterGrade$4213 extends DslOracle {
     /*
      assert dropdown "Grade tooltip".text matches "[A-F]" "Grade dropdown must contain a letter grade".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Grade dropdown must contain a letter grade";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$4167$24 = getWidget("dropdown", "Grade tooltip", state);
        if (widget$4167$24 == null) {
          return Verdict.OK;
        }
        Object widget$4167$29 = getProperty(widget$4167$24, "text");
        boolean cond$4197 = evaluateMatches(widget$4167$29, "[A-F]");
        if (!cond$4197) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$4167$24)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class TableDataMustNeverContainsTheInvalid2100Date$4905 extends DslOracle {
     /*
      assert for all table_data it.text not contains "2100-12-12 20:00" "Table data must never contains the invalid 2100 date".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Table data must never contains the invalid 2100 date";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_data", state)) {
          
          Object widget$4865$7 = getProperty($it, "text");
          boolean cond$4873 = !(evaluateContains(widget$4865$7, "2100-12-12 20:00"));
          if (!cond$4873) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class AllExamDateRowsMustContainADateAndTime$5329 extends DslOracle {
     /*
      assert for all table_row 
        it.text matches "\\b\\d{4}-\\d{2}-\\d{2}\\s?\\d{2}:\\d{2}\\b" 
        when it.identifier contains "table.examDateRow" 
        "All exam date rows must contain a date and time".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "All exam date rows must contain a date and time";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$5282$13 = getProperty($it, "identifier");
          boolean cond$5296 = evaluateContains(widget$5282$13, "table.examDateRow");
          if (cond$5296) {
            
            Object widget$5211$7 = getProperty($it, "text");
            boolean cond$5219 = evaluateMatches(widget$5211$7, "\\b\\d{4}-\\d{2}-\\d{2}\\s?\\d{2}:\\d{2}\\b");
            if (!cond$5219) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class ParticipantsButtonMustNotContainANegativeNumber$5700 extends DslOracle {
     /*
      assert for all button 
        it.text not matches "-\\d+" 
        when it.text contains "Participants" 
        "Participants button must not contain a negative number".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Participants button must not contain a negative number";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("button", state)) {
          
          Object widget$5664$7 = getProperty($it, "text");
          boolean cond$5672 = evaluateContains(widget$5664$7, "Participants");
          if (cond$5672) {
            
            Object widget$5627$7 = getProperty($it, "text");
            boolean cond$5635 = !(evaluateMatches(widget$5627$7, "-\\d+"));
            if (!cond$5635) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class OtherAvailableSubjectsTableMustContainTheFiveCorrectColumnsInOrder$6014 extends DslOracle {
     /*
      assert table "otherSubjects.table".headerText contains "# Subject Teacher(s) Credits Enroll subject" "Other Available Subjects table must contain the five correct columns in order".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Other Available Subjects table must contain the five correct columns in order";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$5920$27 = getWidget("table", "otherSubjects.table", state);
        if (widget$5920$27 == null) {
          return Verdict.OK;
        }
        Object widget$5920$38 = getProperty(widget$5920$27, "headerText");
        boolean cond$5959 = evaluateContains(widget$5920$38, "# Subject Teacher(s) Credits Enroll subject");
        if (!cond$5959) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$5920$27)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class EnrolledSubjectsTableRowsMustContainFiveDataElements$6366 extends DslOracle {
     /*
      assert for all table_row 
        it.data_elements.length is equal to 5 
        when it.identifier contains "enrolledTable.subjectRow" 
        "Enrolled Subjects table rows must contain five data elements".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Enrolled Subjects table rows must contain five data elements";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$6312$13 = getProperty($it, "identifier");
          boolean cond$6326 = evaluateContains(widget$6312$13, "enrolledTable.subjectRow");
          if (cond$6326) {
            
            Object widget$6265$16 = getProperty($it, "data_elements");
            Object widget$6265$23 = getProperty(widget$6265$16, "length");
            boolean cond$6289 = evaluateIsEqualTo(widget$6265$23, 5);
            if (!cond$6289) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class StudentsMustBeEnrolledToSubjectsThatMustContainFiveDataElements$7913 extends DslOracle {
     /*
      assert for all table_row 
        it.data_elements.length is equal to 5 
        when it.identifier contains "mySubjects.enrolledTable" 
        "Students must be enrolled to subjects that must contain five data elements".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Students must be enrolled to subjects that must contain five data elements";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$7859$13 = getProperty($it, "identifier");
          boolean cond$7873 = evaluateContains(widget$7859$13, "mySubjects.enrolledTable");
          if (cond$7873) {
            
            Object widget$7812$16 = getProperty($it, "data_elements");
            Object widget$7812$23 = getProperty(widget$7812$16, "length");
            boolean cond$7836 = evaluateIsEqualTo(widget$7812$23, 5);
            if (!cond$7836) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class TeachersMustTaughtSubjectsThatMustContainSixDataElements$8446 extends DslOracle {
     /*
      assert for all table_row 
        it.data_elements.length is equal to 6 
        when it.identifier contains "mySubjects.table" 
        "Teachers must taught subjects that must contain six data elements".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Teachers must taught subjects that must contain six data elements";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$8400$13 = getProperty($it, "identifier");
          boolean cond$8414 = evaluateContains(widget$8400$13, "mySubjects.table");
          if (cond$8414) {
            
            Object widget$8353$16 = getProperty($it, "data_elements");
            Object widget$8353$23 = getProperty(widget$8353$16, "length");
            boolean cond$8377 = evaluateIsEqualTo(widget$8353$23, 6);
            if (!cond$8377) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class TeachersListRowsMustContainThreeDataElements$8961 extends DslOracle {
     /*
      assert for all table_row 
        it.data_elements.length is equal to 3 
        when it.identifier contains "listOfAllTeachers.table" 
        "Teachers list rows must contain three data elements".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Teachers list rows must contain three data elements";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$8908$13 = getProperty($it, "identifier");
          boolean cond$8922 = evaluateContains(widget$8908$13, "listOfAllTeachers.table");
          if (cond$8922) {
            
            Object widget$8861$16 = getProperty($it, "data_elements");
            Object widget$8861$23 = getProperty(widget$8861$16, "length");
            boolean cond$8885 = evaluateIsEqualTo(widget$8861$23, 3);
            if (!cond$8885) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
     
}