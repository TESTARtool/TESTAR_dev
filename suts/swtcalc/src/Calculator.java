public class Calculator{

    public static void hit(Calculator calc, Keys key){
        calc.hitKey(key);
        System.out.println(calc);
    }

    public static enum Keys{
        Zero, One, Two, Three, Four, Five,
        Six, Seven, Eight, Nine, Plus, Minus,
        Times, Divide, Equals, Cos, Tan, Sin,
        Log, Clear, Sqrt, Negate, Reciproce
    }

    private double numReg;
    private int opReg;
    private double operandReg;
    private String display;

    public Calculator(){
        numReg = 0.0;
        display = "0";
        opReg = -1;
        operandReg = 0.0;
    }

    private boolean isNum(Keys key){ return key != null && key.ordinal() >= 0 && key.ordinal() < 10; }

    public void hitKey(Keys key){
        if(key == null) throw new IllegalArgumentException();

        if(isNum(key)){
            numReg = numReg *  10.0 + key.ordinal();
        }else if(key == Keys.Plus || key == Keys.Minus || key == Keys.Times || key == Keys.Divide){
            eval();
            operandReg = numReg;
            numReg = 0;
            opReg = key.ordinal();
        }else if(key == Keys.Log){
            eval();
            numReg = Math.log(numReg);
        }else if(key == Keys.Clear){
            numReg = 0;
            opReg = 0;
            operandReg = 0;
        }else if(key == Keys.Sqrt){
            eval();
            if(numReg < 0) throw new ArithmeticException("Root of negative number!");
            numReg = Math.sqrt(numReg);
        }else if(key == Keys.Negate){
            eval();
            numReg = -numReg;
        }else if(key == Keys.Reciproce){
            eval();
            numReg = 1.0 / numReg;
        }else if(key == Keys.Equals){
            eval();
        }
        display = Double.toString(numReg);
    }

    private void eval(){
        if(opReg == Keys.Plus.ordinal()){
            numReg = operandReg + numReg;
            operandReg = 0;
            opReg = -1;
            display = Double.toString(numReg);
        } else if(opReg == Keys.Minus.ordinal()){
            numReg = operandReg - numReg;
            operandReg = 0;
            opReg = -1;
            display = Double.toString(numReg);
        } else if(opReg == Keys.Times.ordinal()){
            numReg = operandReg * numReg;
            operandReg = 0;
            opReg = -1;
            display = Double.toString(numReg);
        } else if(opReg == Keys.Divide.ordinal()){
            if(numReg == 0) throw new ArithmeticException("Division by thero!");
            numReg = operandReg / numReg;
            operandReg = 0;
            opReg = -1;
            display = Double.toString(numReg);
        }else{
            display = Double.toString(numReg);
        }
    }

    public String getDisplay(){ return display; }
    public String toString(){ return getDisplay(); }
}
