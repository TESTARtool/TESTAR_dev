package multi;

import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.oracles.Oracle;

public class TBuis_oracles {
   
   public static class LoginButtonMustBeAToneOfBlue$103 implements Oracle {
     /*
      assert button "Login".backgroundColorName contains "blue" "Login button must be a tone of blue".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Login button must be a tone of blue";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$52$14 = getWidget("button", "Login", state);
        if (widget$52$14 == null) {
          return Verdict.OK;
        }
        Object widget$52$34 = getProperty(widget$52$14, "backgroundColorName");
        boolean cond$87 = evaluateContains(widget$52$34, "blue");
        if (!cond$87) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$52$14)); }
        return verdict;
     }
   }
   
   public static class EnrollButtonMustBeAToneOfBlue$202 implements Oracle {
     /*
      assert button "Enroll".backgroundColorName contains "blue" "Enroll button must be a tone of blue".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Enroll button must be a tone of blue";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$150$15 = getWidget("button", "Enroll", state);
        if (widget$150$15 == null) {
          return Verdict.OK;
        }
        Object widget$150$35 = getProperty(widget$150$15, "backgroundColorName");
        boolean cond$186 = evaluateContains(widget$150$35, "blue");
        if (!cond$186) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$150$15)); }
        return verdict;
     }
   }
   
   public static class UpdateButtonMustBeAToneOfGreen$303 implements Oracle {
     /*
      assert button "Update".backgroundColorName contains "green" "Update button must be a tone of green".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Update button must be a tone of green";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$250$15 = getWidget("button", "Update", state);
        if (widget$250$15 == null) {
          return Verdict.OK;
        }
        Object widget$250$35 = getProperty(widget$250$15, "backgroundColorName");
        boolean cond$286 = evaluateContains(widget$250$35, "green");
        if (!cond$286) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$250$15)); }
        return verdict;
     }
   }
   
   public static class UnregisterButtonMustBeExactlyRedCrimson$414 implements Oracle {
     /*
      assert button "Unregister".backgroundColorName is equal to "crimson" "Unregister button must be exactly red crimson".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Unregister button must be exactly red crimson";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$352$19 = getWidget("button", "Unregister", state);
        if (widget$352$19 == null) {
          return Verdict.OK;
        }
        Object widget$352$39 = getProperty(widget$352$19, "backgroundColorName");
        boolean cond$392 = evaluateIsEqualTo(widget$352$39, "crimson");
        if (!cond$392) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$352$19)); }
        return verdict;
     }
   }
   
   public static class ParticipantsButtonMustBeAToneOfBlue$605 implements Oracle {
     /*
      assert button "Participants".backgroundColorName contains "blue" "Participants button must be a tone of blue".
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
        Widget widget$547$21 = getWidget("button", "Participants", state);
        if (widget$547$21 == null) {
          return Verdict.OK;
        }
        Object widget$547$41 = getProperty(widget$547$21, "backgroundColorName");
        boolean cond$589 = evaluateContains(widget$547$41, "blue");
        if (!cond$589) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$547$21)); }
        return verdict;
     }
   }
   
   public static class StudentViewTitleMustContainStudentSViewText$861 implements Oracle {
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
        Widget widget$801$28 = getWidget("static_text", "stu.view.title", state);
        if (widget$801$28 == null) {
          return Verdict.OK;
        }
        Object widget$801$33 = getProperty(widget$801$28, "text");
        boolean cond$835 = evaluateContains(widget$801$33, "Student's View");
        if (!cond$835) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$801$28)); }
        return verdict;
     }
   }
   
   public static class TotalCreditsTextMustNotContainTwentyCredits$1309 implements Oracle {
       /*
        assert static_text "completedTable.creditAmount".text not contains "20 Total Credits" "Total credits text must not contain twenty credits".
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
          Widget widget$1230$41 = getWidget("static_text", "completedTable.creditAmount", state);
          if (widget$1230$41 == null) {
            return Verdict.OK;
          }
          Object widget$1230$46 = getProperty(widget$1230$41, "text");
          boolean cond$1277 = !(evaluateContains(widget$1230$46, "20 Total Credits"));
          if (!cond$1277) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1230$41)); }
          return verdict;
       }
     }
   
   public static class TaughtSubjectsTableMustContainTheSubjectTeacherSOrder$2007 implements Oracle {
     /*
      assert table "mySubjects.table".headerText contains "Subject Teacher(s)" "Taught Subjects table must contain the Subject Teacher(s) order".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Taught Subjects table must contain the Subject Teacher(s) order";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$1941$24 = getWidget("table", "mySubjects.table", state);
        if (widget$1941$24 == null) {
          return Verdict.OK;
        }
        Object widget$1941$35 = getProperty(widget$1941$24, "headerText");
        boolean cond$1977 = evaluateContains(widget$1941$35, "Subject Teacher(s)");
        if (!cond$1977) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1941$24)); }
        return verdict;
     }
   }
   
   public static class TableDataMustNeverContainTheNonameText$2432 implements Oracle {
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
          
          Object widget$2402$7 = getProperty($it, "text");
          boolean cond$2410 = !(evaluateContains(widget$2402$7, "Noname"));
          if (!cond$2410) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class GradeDropdownMustContainALetterGrade$3984 implements Oracle {
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
          Widget widget$3938$24 = getWidget("dropdown", "Grade tooltip", state);
          if (widget$3938$24 == null) {
            return Verdict.OK;
          }
          Object widget$3938$29 = getProperty(widget$3938$24, "text");
          boolean cond$3968 = evaluateMatches(widget$3938$29, "[A-F]");
          if (!cond$3968) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$3938$24)); }
          return verdict;
       }
     }
   
   public static class TableDataMustNeverContainsTheInvalid2100Date$4663 implements Oracle {
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
          
          Object widget$4623$7 = getProperty($it, "text");
          boolean cond$4631 = !(evaluateContains(widget$4623$7, "2100-12-12 20:00"));
          if (!cond$4631) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), $it)); }
        }
        return verdict;
     }
   }
   
   public static class AllExamDateRowsMustContainADateAndTime$5049 implements Oracle {
       /*
        assert table_row "table.examDateRow".text matches "\\b\\d{4}-\\d{2}-\\d{2}\\s?\\d{2}:\\d{2}\\b" "All exam date rows must contain a date and time".
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
          Widget widget$4960$29 = getWidget("table_row", "table.examDateRow", state);
          if (widget$4960$29 == null) {
            return Verdict.OK;
          }
          Object widget$4960$34 = getProperty(widget$4960$29, "text");
          boolean cond$4995 = evaluateMatches(widget$4960$34, "\\b\\d{4}-\\d{2}-\\d{2}\\s?\\d{2}:\\d{2}\\b");
          if (!cond$4995) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$4960$29)); }
          return verdict;
       }
     }
   
   public static class ParticipantsButtonMustNotContainANegativeNumber$5359 implements Oracle {
     /*
      assert button "Participants".text not matches "-\\d+" "Participants button must not contain a negative number".
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
        Widget widget$5312$21 = getWidget("button", "Participants", state);
        if (widget$5312$21 == null) {
          return Verdict.OK;
        }
        Object widget$5312$26 = getProperty(widget$5312$21, "text");
        boolean cond$5339 = !(evaluateMatches(widget$5312$26, "-\\d+"));
        if (!cond$5339) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$5312$21)); }
        return verdict;
     }
   }
   
   public static class OtherAvailableSubjectsTableMustContainTheTeacherSColumn$5640 implements Oracle {
     /*
      assert table "otherSubjects.table".headerText contains "Teacher(s)" "Other Available Subjects table must contain the Teacher(s) column".
     */
   
     @Override
     public void initialize() { }
   
     @Override
     public String getMessage() {
       return "Other Available Subjects table must contain the Teacher(s) column";
     }
   
     @Override
     public Verdict getVerdict(State state) {
        Verdict verdict = Verdict.OK;
        Widget widget$5579$27 = getWidget("table", "otherSubjects.table", state);
        if (widget$5579$27 == null) {
          return Verdict.OK;
        }
        Object widget$5579$38 = getProperty(widget$5579$27, "headerText");
        boolean cond$5618 = evaluateContains(widget$5579$38, "Teacher(s)");
        if (!cond$5618) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$5579$27)); }
        return verdict;
     }
   }
   
   public static class EnrolledSubjectsTableRowsMustContainFiveDataElements$5929 implements Oracle {
     /*
      assert table_row "enrolledTable.subjectRow".data_elements.length is equal to 5 "Enrolled Subjects table rows must contain five data elements".
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
        Widget widget$5857$36 = getWidget("table_row", "enrolledTable.subjectRow", state);
        if (widget$5857$36 == null) {
          return Verdict.OK;
        }
        Object widget$5857$50 = getProperty(widget$5857$36, "data_elements");
        Object widget$5857$57 = getProperty(widget$5857$50, "length");
        boolean cond$5915 = evaluateIsEqualTo(widget$5857$57, 5);
        if (!cond$5915) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$5857$36)); }
        return verdict;
     }
   }
   
   public static class StudentsMustBeEnrolledToSubjectsThatMustContainFiveDataElements$7425 implements Oracle {
     /*
      assert table_row "mySubjects.enrolledTable".data_elements.length is equal to 5 "Students must be enrolled to subjects that must contain five data elements".
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
        Widget widget$7353$36 = getWidget("table_row", "mySubjects.enrolledTable", state);
        if (widget$7353$36 == null) {
          return Verdict.OK;
        }
        Object widget$7353$50 = getProperty(widget$7353$36, "data_elements");
        Object widget$7353$57 = getProperty(widget$7353$50, "length");
        boolean cond$7411 = evaluateIsEqualTo(widget$7353$57, 5);
        if (!cond$7411) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$7353$36)); }
        return verdict;
     }
   }
   
   public static class TeachersMustTaughtSubjectsThatMustContainSixDataElements$7907 implements Oracle {
     /*
      assert table_row "mySubjects.table".data_elements.length is equal to 6 "Teachers must taught subjects that must contain six data elements".
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
        Widget widget$7843$28 = getWidget("table_row", "mySubjects.table", state);
        if (widget$7843$28 == null) {
          return Verdict.OK;
        }
        Object widget$7843$42 = getProperty(widget$7843$28, "data_elements");
        Object widget$7843$49 = getProperty(widget$7843$42, "length");
        boolean cond$7893 = evaluateIsEqualTo(widget$7843$49, 6);
        if (!cond$7893) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$7843$28)); }
        return verdict;
     }
   }
   
   public static class TeachersListRowsMustContainThreeDataElements$8371 implements Oracle {
     /*
      assert table_row "listOfAllTeachers.table".data_elements.length is equal to 3 "Teachers list rows must contain three data elements".
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
        Widget widget$8300$35 = getWidget("table_row", "listOfAllTeachers.table", state);
        if (widget$8300$35 == null) {
          return Verdict.OK;
        }
        Object widget$8300$49 = getProperty(widget$8300$35, "data_elements");
        Object widget$8300$56 = getProperty(widget$8300$49, "length");
        boolean cond$8357 = evaluateIsEqualTo(widget$8300$56, 3);
        if (!cond$8357) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$8300$35)); }
        return verdict;
     }
   }
     
}