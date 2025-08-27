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
   
   public static class StudentViewTitleMustContainStudentSViewText$795 implements Oracle {
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
          Widget widget$735$28 = getWidget("static_text", "stu.view.title", state);
          if (widget$735$28 == null) {
            return Verdict.OK;
          }
          Object widget$735$33 = getProperty(widget$735$28, "text");
          boolean cond$769 = evaluateContains(widget$735$33, "Student's View");
          if (!cond$769) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$735$28)); }
          return verdict;
       }
     }
   
   public static class TaughtSubjectsTableMustContainTheSubjectTeacherSOrder$1219 implements Oracle {
       /*
        assert table "mySubjects".headerText contains "Subject Teacher(s)" "Taught Subjects table must contain the Subject Teacher(s) order".
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
          Widget widget$1159$18 = getWidget("table", "mySubjects", state);
          if (widget$1159$18 == null) {
            return Verdict.OK;
          }
          Object widget$1159$29 = getProperty(widget$1159$18, "headerText");
          boolean cond$1189 = evaluateContains(widget$1159$29, "Subject Teacher(s)");
          if (!cond$1189) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), widget$1159$18)); }
          return verdict;
       }
     }
   
}