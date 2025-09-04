package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class TBuis_oracles {
   
   public static class ParticipantsButtonMustBeAToneOfBlue$229 implements Oracle {
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
            if (!cond$168) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
   }
   
   public static class StudentViewTitleMustContainStudentSViewText$485 implements Oracle {
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
        if (!cond$459) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$425$28)); }
        return verdict;
     }
   }
   
   public static class TotalCreditsTextMustNotContainTwentyCredits$919 implements Oracle {
     /*
      assert static_text "Total Credits".text not contains "20 Total Credits" "Total credits text must not contain twenty credits".
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
        Widget widget$854$27 = getWidget("static_text", "Total Credits", state);
        if (widget$854$27 == null) {
          return Verdict.OK;
        }
        Object widget$854$32 = getProperty(widget$854$27, "text");
        boolean cond$887 = !(evaluateContains(widget$854$32, "20 Total Credits"));
        if (!cond$887) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$854$27)); }
        return verdict;
     }
   }
   
   public static class MySubjectsLinkMustBeSelectedWithAToneOfBlueWhenSubjectSuccessfullyRemoveTextIsVisible$1292 implements Oracle {
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
        Widget widget$1224$52 = getWidget("static_text", "Subject has been successfully removed!", state);
        if (widget$1224$52 == null) {
          return Verdict.OK;
        }
        boolean cond$1277 = evaluateIsStatus(widget$1224$52, "visible");
        if (cond$1277) {
          Widget widget$1160$18 = getWidget("link", "My Subjects", state);
          if (widget$1160$18 == null) {
            return Verdict.OK;
          }
          Object widget$1160$38 = getProperty(widget$1160$18, "backgroundColorName");
          boolean cond$1199 = evaluateContains(widget$1160$38, "blue");
          if (!cond$1199) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1160$18)); }
        }
        return verdict;
     }
   }
   
   public static class TaughtSubjectsTableMustContainTheSixCorrectColumnsInOrder$1932 implements Oracle {
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
        Widget widget$1818$24 = getWidget("table", "mySubjects.table", state);
        if (widget$1818$24 == null) {
          return Verdict.OK;
        }
        Object widget$1818$35 = getProperty(widget$1818$24, "headerText");
        boolean cond$1854 = evaluateContains(widget$1818$35, "# Subject Teacher(s) New exam date List of students Remove subject");
        if (!cond$1854) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1818$24)); }
        return verdict;
     }
   }
   
   public static class TableDataMustNeverContainTheNonameText$2361 implements Oracle {
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
          
          Object widget$2331$7 = getProperty($it, "text");
          boolean cond$2339 = !(evaluateContains(widget$2331$7, "Noname"));
          if (!cond$2339) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class GradeDropdownMustContainALetterGrade$3902 implements Oracle {
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
        Widget widget$3856$24 = getWidget("dropdown", "Grade tooltip", state);
        if (widget$3856$24 == null) {
          return Verdict.OK;
        }
        Object widget$3856$29 = getProperty(widget$3856$24, "text");
        boolean cond$3886 = evaluateMatches(widget$3856$29, "[A-F]");
        if (!cond$3886) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$3856$24)); }
        return verdict;
     }
   }
   
   public static class TableDataMustNeverContainsTheInvalid2100Date$4594 implements Oracle {
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
          
          Object widget$4554$7 = getProperty($it, "text");
          boolean cond$4562 = !(evaluateContains(widget$4554$7, "2100-12-12 20:00"));
          if (!cond$4562) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class AllExamDateRowsMustContainADateAndTime$5018 implements Oracle {
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
          
          Object widget$4971$13 = getProperty($it, "identifier");
          boolean cond$4985 = evaluateContains(widget$4971$13, "table.examDateRow");
          if (cond$4985) {
            
            Object widget$4900$7 = getProperty($it, "text");
            boolean cond$4908 = evaluateMatches(widget$4900$7, "\\b\\d{4}-\\d{2}-\\d{2}\\s?\\d{2}:\\d{2}\\b");
            if (!cond$4908) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
   }
   
   public static class ParticipantsButtonMustNotContainANegativeNumber$5389 implements Oracle {
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
          
          Object widget$5353$7 = getProperty($it, "text");
          boolean cond$5361 = evaluateContains(widget$5353$7, "Participants");
          if (cond$5361) {
            
            Object widget$5316$7 = getProperty($it, "text");
            boolean cond$5324 = !(evaluateMatches(widget$5316$7, "-\\d+"));
            if (!cond$5324) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
   }
   
   public static class OtherAvailableSubjectsTableMustContainTheFiveCorrectColumnsInOrder$5703 implements Oracle {
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
        Widget widget$5609$27 = getWidget("table", "otherSubjects.table", state);
        if (widget$5609$27 == null) {
          return Verdict.OK;
        }
        Object widget$5609$38 = getProperty(widget$5609$27, "headerText");
        boolean cond$5648 = evaluateContains(widget$5609$38, "# Subject Teacher(s) Credits Enroll subject");
        if (!cond$5648) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$5609$27)); }
        return verdict;
     }
   }
   
   public static class EnrolledSubjectsTableRowsMustContainFiveDataElements$6055 implements Oracle {
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
          
          Object widget$6001$13 = getProperty($it, "identifier");
          boolean cond$6015 = evaluateContains(widget$6001$13, "enrolledTable.subjectRow");
          if (cond$6015) {
            
            Object widget$5954$16 = getProperty($it, "data_elements");
            Object widget$5954$23 = getProperty(widget$5954$16, "length");
            boolean cond$5978 = evaluateIsEqualTo(widget$5954$23, 5);
            if (!cond$5978) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
   }
   
   public static class StudentsMustBeEnrolledToSubjectsThatMustContainFiveDataElements$7602 implements Oracle {
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
          
          Object widget$7548$13 = getProperty($it, "identifier");
          boolean cond$7562 = evaluateContains(widget$7548$13, "mySubjects.enrolledTable");
          if (cond$7562) {
            
            Object widget$7501$16 = getProperty($it, "data_elements");
            Object widget$7501$23 = getProperty(widget$7501$16, "length");
            boolean cond$7525 = evaluateIsEqualTo(widget$7501$23, 5);
            if (!cond$7525) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
   }
   
   public static class TeachersMustTaughtSubjectsThatMustContainSixDataElements$8135 implements Oracle {
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
          
          Object widget$8089$13 = getProperty($it, "identifier");
          boolean cond$8103 = evaluateContains(widget$8089$13, "mySubjects.table");
          if (cond$8103) {
            
            Object widget$8042$16 = getProperty($it, "data_elements");
            Object widget$8042$23 = getProperty(widget$8042$16, "length");
            boolean cond$8066 = evaluateIsEqualTo(widget$8042$23, 6);
            if (!cond$8066) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
   }
   
   public static class TeachersListRowsMustContainThreeDataElements$8650 implements Oracle {
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
          
          Object widget$8597$13 = getProperty($it, "identifier");
          boolean cond$8611 = evaluateContains(widget$8597$13, "listOfAllTeachers.table");
          if (cond$8611) {
            
            Object widget$8550$16 = getProperty($it, "data_elements");
            Object widget$8550$23 = getProperty(widget$8550$16, "length");
            boolean cond$8574 = evaluateIsEqualTo(widget$8550$23, 3);
            if (!cond$8574) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
          }
        }
        return verdict;
     }
   }
     
}