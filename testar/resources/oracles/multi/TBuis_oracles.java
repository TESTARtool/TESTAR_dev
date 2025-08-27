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
     
}