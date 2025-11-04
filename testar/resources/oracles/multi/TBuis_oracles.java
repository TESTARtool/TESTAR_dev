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
   
   public static class TotalCreditsTextMustNotContainTwentyCredits$1232 extends DslOracle {
     /*
      assert static_text "Total Credits".text is equal to "Total Credits 0" 
        when table "mySubjects.completedTable".rows.length is equal to 0 "Total credits text must not contain twenty credits".
     */
   
     @Override
     public String getMessage() {
       return "Total credits text must not contain twenty credits";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1172$33 = getWidget("table", "mySubjects.completedTable", state);
        if (widget$1172$33 == null) {
          return Verdict.OK;
        }
        Object widget$1172$38 = getProperty(widget$1172$33, "rows");
        Object widget$1172$45 = getProperty(widget$1172$38, "length");
        boolean cond$1218 = evaluateIsEqualTo(widget$1172$45, 0);
        if (cond$1218) {
          Widget widget$1100$27 = getWidget("static_text", "Total Credits", state);
          if (widget$1100$27 == null) {
            return Verdict.OK;
          }
          Object widget$1100$32 = getProperty(widget$1100$27, "text");
          boolean cond$1133 = evaluateIsEqualTo(widget$1100$32, "Total Credits 0");
          if (!cond$1133) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1100$27)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class MySubjectsLinkMustBeSelectedWithAToneOfBlueWhenSubjectSuccessfullyRemoveTextIsVisible$1605 extends DslOracle {
     /*
      assert link "My Subjects".backgroundColorName contains "blue" 
        when static_text "Subject has been successfully removed!" is visible 
        "My Subjects link must be selected with a tone of blue when subject successfully remove text is visible".
     */
   
     @Override
     public String getMessage() {
       return "My Subjects link must be selected with a tone of blue when subject successfully remove text is visible";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1537$52 = getWidget("static_text", "Subject has been successfully removed!", state);
        if (widget$1537$52 == null) {
          return Verdict.OK;
        }
        boolean cond$1590 = evaluateIsStatus(widget$1537$52, "visible");
        if (cond$1590) {
          Widget widget$1473$18 = getWidget("link", "My Subjects", state);
          if (widget$1473$18 == null) {
            return Verdict.OK;
          }
          Object widget$1473$38 = getProperty(widget$1473$18, "backgroundColorName");
          boolean cond$1512 = evaluateContains(widget$1473$38, "blue");
          if (!cond$1512) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1473$18)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class TaughtSubjectsTableMustContainTheSixCorrectColumnsInOrder$2245 extends DslOracle {
     /*
      assert table "mySubjects.table".headerText contains "# Subject Teacher(s) New exam date List of students Remove subject" "Taught Subjects table must contain the six correct columns in order".
     */
   
     @Override
     public String getMessage() {
       return "Taught Subjects table must contain the six correct columns in order";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$2131$24 = getWidget("table", "mySubjects.table", state);
        if (widget$2131$24 == null) {
          return Verdict.OK;
        }
        Object widget$2131$35 = getProperty(widget$2131$24, "headerText");
        boolean cond$2167 = evaluateContains(widget$2131$35, "# Subject Teacher(s) New exam date List of students Remove subject");
        if (!cond$2167) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$2131$24)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class TableDataMustNeverContainTheNonameText$2674 extends DslOracle {
     /*
      assert for all table_data it.text not contains "Noname" "Table data must never contain the Noname text".
     */
   
     @Override
     public String getMessage() {
       return "Table data must never contain the Noname text";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_data", state)) {
          
          Object widget$2644$7 = getProperty($it, "text");
          boolean cond$2652 = !(evaluateContains(widget$2644$7, "Noname"));
          if (!cond$2652) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class GradeDropdownMustContainALetterGrade$4215 extends DslOracle {
     /*
      assert dropdown "Grade tooltip".text matches "[A-F]" "Grade dropdown must contain a letter grade".
     */
   
     @Override
     public String getMessage() {
       return "Grade dropdown must contain a letter grade";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$4169$24 = getWidget("dropdown", "Grade tooltip", state);
        if (widget$4169$24 == null) {
          return Verdict.OK;
        }
        Object widget$4169$29 = getProperty(widget$4169$24, "text");
        boolean cond$4199 = evaluateMatches(widget$4169$29, "[A-F]");
        if (!cond$4199) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$4169$24)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class TableDataMustNeverContainsTheInvalid2100Date$4907 extends DslOracle {
     /*
      assert for all table_data it.text not contains "2100-12-12 20:00" "Table data must never contains the invalid 2100 date".
     */
   
     @Override
     public String getMessage() {
       return "Table data must never contains the invalid 2100 date";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_data", state)) {
          
          Object widget$4867$7 = getProperty($it, "text");
          boolean cond$4875 = !(evaluateContains(widget$4867$7, "2100-12-12 20:00"));
          if (!cond$4875) { 
            verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
          }
          markAsNonVacuous();
        }
        return verdict;
     }
   }
   
   public static class AllExamDateRowsMustContainADateAndTime$5331 extends DslOracle {
     /*
      assert for all table_row 
        it.text matches "\\b\\d{4}-\\d{2}-\\d{2}\\s?\\d{2}:\\d{2}\\b" 
        when it.identifier contains "table.examDateRow" 
        "All exam date rows must contain a date and time".
     */
   
     @Override
     public String getMessage() {
       return "All exam date rows must contain a date and time";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$5284$13 = getProperty($it, "identifier");
          boolean cond$5298 = evaluateContains(widget$5284$13, "table.examDateRow");
          if (cond$5298) {
            
            Object widget$5213$7 = getProperty($it, "text");
            boolean cond$5221 = evaluateMatches(widget$5213$7, "\\b\\d{4}-\\d{2}-\\d{2}\\s?\\d{2}:\\d{2}\\b");
            if (!cond$5221) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class ParticipantsButtonMustNotContainANegativeNumber$5702 extends DslOracle {
     /*
      assert for all button 
        it.text not matches "-\\d+" 
        when it.text contains "Participants" 
        "Participants button must not contain a negative number".
     */
   
     @Override
     public String getMessage() {
       return "Participants button must not contain a negative number";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("button", state)) {
          
          Object widget$5666$7 = getProperty($it, "text");
          boolean cond$5674 = evaluateContains(widget$5666$7, "Participants");
          if (cond$5674) {
            
            Object widget$5629$7 = getProperty($it, "text");
            boolean cond$5637 = !(evaluateMatches(widget$5629$7, "-\\d+"));
            if (!cond$5637) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class OtherAvailableSubjectsTableMustContainTheFiveCorrectColumnsInOrder$6016 extends DslOracle {
     /*
      assert table "otherSubjects.table".headerText contains "# Subject Teacher(s) Credits Enroll subject" "Other Available Subjects table must contain the five correct columns in order".
     */
   
     @Override
     public String getMessage() {
       return "Other Available Subjects table must contain the five correct columns in order";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$5922$27 = getWidget("table", "otherSubjects.table", state);
        if (widget$5922$27 == null) {
          return Verdict.OK;
        }
        Object widget$5922$38 = getProperty(widget$5922$27, "headerText");
        boolean cond$5961 = evaluateContains(widget$5922$38, "# Subject Teacher(s) Credits Enroll subject");
        if (!cond$5961) { 
          verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$5922$27)); 
        }
        markAsNonVacuous();
        return verdict;
     }
   }
   
   public static class EnrolledSubjectsTableRowsMustContainFiveDataElements$6368 extends DslOracle {
     /*
      assert for all table_row 
        it.data_elements.length is equal to 5 
        when it.identifier contains "enrolledTable.subjectRow" 
        "Enrolled Subjects table rows must contain five data elements".
     */
   
     @Override
     public String getMessage() {
       return "Enrolled Subjects table rows must contain five data elements";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$6314$13 = getProperty($it, "identifier");
          boolean cond$6328 = evaluateContains(widget$6314$13, "enrolledTable.subjectRow");
          if (cond$6328) {
            
            Object widget$6267$16 = getProperty($it, "data_elements");
            Object widget$6267$23 = getProperty(widget$6267$16, "length");
            boolean cond$6291 = evaluateIsEqualTo(widget$6267$23, 5);
            if (!cond$6291) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class StudentsMustBeEnrolledToSubjectsThatMustContainFiveDataElements$7915 extends DslOracle {
     /*
      assert for all table_row 
        it.data_elements.length is equal to 5 
        when it.identifier contains "mySubjects.enrolledTable" 
        "Students must be enrolled to subjects that must contain five data elements".
     */
   
     @Override
     public String getMessage() {
       return "Students must be enrolled to subjects that must contain five data elements";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$7861$13 = getProperty($it, "identifier");
          boolean cond$7875 = evaluateContains(widget$7861$13, "mySubjects.enrolledTable");
          if (cond$7875) {
            
            Object widget$7814$16 = getProperty($it, "data_elements");
            Object widget$7814$23 = getProperty(widget$7814$16, "length");
            boolean cond$7838 = evaluateIsEqualTo(widget$7814$23, 5);
            if (!cond$7838) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class TeachersMustTaughtSubjectsThatMustContainSixDataElements$8448 extends DslOracle {
     /*
      assert for all table_row 
        it.data_elements.length is equal to 6 
        when it.identifier contains "mySubjects.table" 
        "Teachers must taught subjects that must contain six data elements".
     */
   
     @Override
     public String getMessage() {
       return "Teachers must taught subjects that must contain six data elements";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$8402$13 = getProperty($it, "identifier");
          boolean cond$8416 = evaluateContains(widget$8402$13, "mySubjects.table");
          if (cond$8416) {
            
            Object widget$8355$16 = getProperty($it, "data_elements");
            Object widget$8355$23 = getProperty(widget$8355$16, "length");
            boolean cond$8379 = evaluateIsEqualTo(widget$8355$23, 6);
            if (!cond$8379) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
   
   public static class TeachersListRowsMustContainThreeDataElements$8963 extends DslOracle {
     /*
      assert for all table_row 
        it.data_elements.length is equal to 3 
        when it.identifier contains "listOfAllTeachers.table" 
        "Teachers list rows must contain three data elements".
     */
   
     @Override
     public String getMessage() {
       return "Teachers list rows must contain three data elements";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        for (Widget $it: getWidgets("table_row", state)) {
          
          Object widget$8910$13 = getProperty($it, "identifier");
          boolean cond$8924 = evaluateContains(widget$8910$13, "listOfAllTeachers.table");
          if (cond$8924) {
            
            Object widget$8863$16 = getProperty($it, "data_elements");
            Object widget$8863$23 = getProperty(widget$8863$16, "length");
            boolean cond$8887 = evaluateIsEqualTo(widget$8863$23, 3);
            if (!cond$8887) { 
              verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); 
            }
            markAsNonVacuous();
          }
        }
        return verdict;
     }
   }
     
}