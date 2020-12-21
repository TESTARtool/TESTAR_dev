//Spaghetti program to test 1 jar, 1 class
// With many as different possible programming activities 
// vandaar ook dat actionListeners op twee verschillende 


import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javax.swing.JRadioButton;

import java.awt.event.*;


public class TestGUI extends JFrame implements ActionListener, ItemListener{
	
	static final long serialVersionUID = 1;
	
	int numClicks;
	int numInvisibleClicks;
	boolean boolSwapper;
	boolean boolPictureSwapper;
	
	JTextField tfBelow;
	JTextArea taCentral;
	JTextField number1;
	JTextField number2;
	JTextField result;
	
	JPanel panelLeft;
	
	JLabel bgL;
	JPanel panelRight;
	JLabel lblSign;
	
	JTextField tfXTimes;
    JTextField tfYTable;
    
    float fPizzaPrice;
    float fToppingPrice;
    int nPizzasOrdered;
    
    JCheckBox standardButton;
    JCheckBox saladButton;
    JCheckBox grandeButton;
    int priceExtras;
    int price1;
    int price2;
    int price3;
    
	
//	Image img;
    private JTextField filename = new JTextField(), dir = new JTextField();
	
    public TestGUI() {
    	initValues();
        initGUI();
    }
    
    
    public void initValues()
    {
    	numClicks = 0;
    	numInvisibleClicks = numClicks;
    	boolSwapper = true;
    	boolPictureSwapper = false;
    }

    
    // Construct the GUI    
    private void initGUI() {
    	
        //Creating the Frame
        JFrame frame = new JFrame("SPAGHETTI UNDER TEST");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        //=============================================================================
        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        mb.setBackground(Color.yellow);
        JMenu m1 = new JMenu("FILE");
        JMenu m2 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        JMenuItem m1_1 = new JMenuItem("Open");
        m1_1.addActionListener(this);
        JMenuItem m1_2 = new JMenuItem("Save as");
        m1_2.addActionListener(this);
        m1.add(m1_1);
        m1.add(m1_2);
        JMenuItem m2_1 = new JMenuItem("Info");
        m2_1.addActionListener(this);
        m2.add(m2_1);

        //-===========================================================================
        // Creating the panel at the bottom and adding components
        // BOTTUM BAR WITH SEND SECTION, COMMUNICATES WITH CENTRAL TEXT SECTION
        JPanel panelBelow = new JPanel(); // the panel is not visible in output
        panelBelow.setBackground(Color.green);
        JLabel label = new JLabel("Enter Text");
        tfBelow = new JTextField(50); // accepts upto 10 characters
        JButton send = new JButton("Send");
        send.addActionListener(this);
        JButton reset = new JButton("Reset Text");
        reset.addActionListener(this);
        JButton resetTextArea = new JButton("Reset TextArea");
        resetTextArea.addActionListener(this);
        panelBelow.add(label); // Components Added using Flow Layout
        panelBelow.add(tfBelow);
        panelBelow.add(send);
        panelBelow.add(reset);
        panelBelow.add(resetTextArea);
        
  
        //=============================================================================  
        // Creating the panel at Right and adding components
        // CALCULATOR SECTION
        panelRight = new JPanel(); // the panel is not visible in output
        panelRight.setPreferredSize(new Dimension(300, 40));
        //panelRight.setMinimumSize(new Dimension(300, 40));
        panelRight.setBackground(Color.red);

        JPanel panelTopRight = new JPanel();
        panelTopRight.setPreferredSize(new Dimension(300, 250));
        panelTopRight.setBackground(Color.red);
        
        JLabel lblTopCalculator = new JLabel("Calculator Panel");
        lblTopCalculator.setSize(200, 100);
        lblTopCalculator.setFont(new Font("Courier New", Font.ITALIC, 24));
        lblTopCalculator.setForeground(Color.white);
        
        JLabel lblNumber1 = new JLabel("Type in number1: ");
        lblTopCalculator.setSize(100, 15);
        number1 = new JTextField(10);
        Font myFontSize = number1.getFont().deriveFont(Font.BOLD,30f);
        number1.setFont(myFontSize);

        JLabel lblNumber2 = new JLabel("Type in number2: ");
        number2 = new JTextField(10);
        myFontSize = number2.getFont().deriveFont(Font.BOLD,30f);
        number2.setFont(myFontSize);

        JLabel lblResult = new JLabel("Result = ");
        lblNumber2.setSize(100, 15);
        result = new JTextField(10);
        myFontSize = result.getFont().deriveFont(Font.BOLD,30f);
        result.setFont(myFontSize);

        panelTopRight.add(lblTopCalculator);
        panelTopRight.add(lblNumber1);
        panelTopRight.add(number1);
        panelTopRight.add(lblNumber2);
        panelTopRight.add(number2);
        panelTopRight.add(lblResult);
        panelTopRight.add(result);
        
        
         
        // Panel bottumRight
        JPanel panelBottumRight = new JPanel();
        panelBottumRight.setLayout(new GridLayout(3,3));
        
        // Buttons with action listeners, first way to add action listeners
        final JButton btnAdd = new JButton("+");
        btnAdd.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		add();
        	}
        });
        
        
       final JButton btnSubstract = new JButton("-");
       btnSubstract.addActionListener(new ActionListener(){
       	public void actionPerformed(ActionEvent e)
       	{
       		substract();
       	}
       });
        
        
        final JButton btnMultiply = new JButton("*");
        btnMultiply.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		multiply();
        	}
        });
        
        
        final JButton btnDevide = new JButton("/");
        btnDevide.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		devide();
        	}
        });
        
        
        // Buttons with action listeners, first way to add action listeners
        final JButton btnMod = new JButton("%");
        btnMod.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		modulus();
        	}
        });
        
        
       final JButton btnPow = new JButton("^");
       btnPow.addActionListener(new ActionListener(){
       	public void actionPerformed(ActionEvent e)
       	{
       		power();
       	}
       });
        
        
        final JButton btnRoot = new JButton("V");
        btnRoot.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		root();
        	}
        });
        
        
        final JButton btnClear = new JButton("C");
        btnClear.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		clear();
        	}
        });
        
        final JButton btnAverage = new JButton("Average");
        btnAverage.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		average();
        	}
        });
        panelBottumRight.add(btnAdd);
        panelBottumRight.add(btnSubstract);
        panelBottumRight.add(btnMultiply);
       	panelBottumRight.add(btnDevide);
        panelBottumRight.add(btnMod);
        panelBottumRight.add(btnPow);
        panelBottumRight.add(btnRoot);
       	panelBottumRight.add(btnClear);
       	panelBottumRight.add(btnAverage);
        
        
        panelRight.add(panelTopRight);
        panelRight.add(panelBottumRight);
        
        
        //====================================================================================================
        // SPAGHETTI MIX SECTION         
        //Creating the panel at the left and adding components 
        // PANEL LEFT
        
        panelLeft = new JPanel(); // the panel is not visible in output
        panelLeft.setPreferredSize(new Dimension(300, 40));
        //panelRight.setMinimumSize(new Dimension(300, 40));
        panelLeft.setBackground(Color.red);

        JPanel panelTopLeft = new JPanel();
        panelTopLeft.setPreferredSize(new Dimension(300, 150));
        panelTopLeft.setBackground(Color.red);
        
        bgL = new JLabel(new ImageIcon(getClass().getResource("/images/spaghetti.png")));
       
        JPanel panelBottumLeft = new JPanel(); 
        panelBottumLeft.setPreferredSize(new Dimension(300, 150));
        panelBottumLeft.setBackground(Color.red);
        
        JLabel lblTopMix = new JLabel("Spaghetti Mix Panel");
        lblTopMix.setSize(100, 100);
        lblTopMix.setFont(new Font("Courier New", Font.ITALIC, 24));
        lblTopMix.setForeground(Color.white);
        
        JTextField tfLeft = new JTextField(10); // accepts upto 10 characters
        JButton btnChangeBackcolor = new JButton("CHANGE BACKGROUND COLOR");
        btnChangeBackcolor.addActionListener(this);
        JButton btnChangePicture = new JButton("PICTURE ON/OFF");
        btnChangePicture.addActionListener(this);
        JButton btnTenTable = new JButton("10 Table");
        btnTenTable.addActionListener(this);
        
        tfXTimes = new JTextField(5); // accepts upto 5 characters
        tfYTable = new JTextField(5); 
        JButton btnPrintTables = new JButton("Print Y-table X times");
        btnPrintTables.addActionListener(this);
       
        lblSign = new JLabel("OUTPUT");
        lblSign.setSize(100, 100);
        lblSign.setFont(new Font("Courier New", Font.ITALIC, 24));
        lblSign.setBackground(Color.black);
        lblSign.setForeground(Color.white);
        // create a lineborder with the specified color and width
        Border border = BorderFactory.createLineBorder(Color.BLACK, 3);
         // set the border of the label
        lblSign.setBorder(border);

        JLabel lblX  = new JLabel("X= ");
        JLabel lblY  = new JLabel("Y= ");
        
        JButton btnInvisible = new JButton("Make me Invisible");
        btnInvisible.addActionListener(this);

        panelTopLeft.add(lblTopMix);
        panelTopLeft.add(tfLeft); 
        panelRight.add(bgL);
        panelTopLeft.add(btnChangeBackcolor);
        panelTopLeft.add(btnTenTable);
        panelTopLeft.add(btnChangePicture);
        panelBottumLeft.add(lblX);
        panelBottumLeft.add(tfXTimes);
        panelBottumLeft.add(lblY);
        panelBottumLeft.add(tfYTable);
        panelBottumLeft.add(btnPrintTables);
        
        panelBottumLeft.add(lblSign);
        panelBottumLeft.add(btnInvisible);
        
        JButton buttonDialog = new JButton();
        buttonDialog.setText("Show dialog!");
        panelBottumLeft.add(buttonDialog);
        buttonDialog.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String name = JOptionPane.showInputDialog(panelLeft,
                        "Whats your name?", "");
                writeName(name);
            }
        });
        
        
     
        
        //--------------------------------------------------------------------------------
        
        JPanel extraBottumLeft = new JPanel();
        extraBottumLeft.setPreferredSize(new Dimension(300, 150));
        extraBottumLeft.setBackground(Color.red);
        
        
        JLabel lblSpagOrder = new JLabel("Order Spaghetti Size + Extras");
        lblSpagOrder.setSize(100, 100);
        lblSpagOrder.setFont(new Font("Courier New", Font.ITALIC, 16));
        lblSpagOrder.setForeground(Color.white);
        
        extraBottumLeft.add(lblSpagOrder);

        // Create RadioButtons
        JRadioButton oneButton = makeRadioButton("Small", false, 9.00f);
        JRadioButton twoButton = makeRadioButton("Medium", false, 10.50f);
        JRadioButton threeButton = makeRadioButton("Large", false, 14);

        //Group the radio buttons.
        ButtonGroup rbGroup = new ButtonGroup();
        rbGroup.add(oneButton);
        rbGroup.add(twoButton);
        rbGroup.add(threeButton);

        extraBottumLeft.add(oneButton);
        extraBottumLeft.add(twoButton);
        extraBottumLeft.add(threeButton);
 
        //-----------------------------
        //Create the check boxes.
        standardButton = new JCheckBox("standard");
        standardButton.setMnemonic(KeyEvent.VK_C);
        standardButton.setSelected(false);

        saladButton = new JCheckBox("Salad");
        saladButton.setMnemonic(KeyEvent.VK_G);
        saladButton.setSelected(false);

        grandeButton = new JCheckBox("Grande");
        grandeButton.setMnemonic(KeyEvent.VK_H);
        grandeButton.setSelected(false);
        
        //Register a listener for the check boxes.
        standardButton.addItemListener(this);
        saladButton.addItemListener(this);
        grandeButton.addItemListener(this);
        
        //Put the check boxes in a column in a panel
        JPanel checkPanel = new JPanel(new GridLayout(0, 1));
        checkPanel.add(standardButton);
        checkPanel.add(saladButton);
        checkPanel.add(grandeButton);

        extraBottumLeft.add(checkPanel, BorderLayout.LINE_START);
        
        // Create Total Button ------------------------------------------------------------------
        JButton buttonTotal = new JButton();
        buttonTotal.setText("Bereken Totaal");
        extraBottumLeft.add(buttonTotal);
        buttonTotal.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taCentral.setText("Total price: " + (fPizzaPrice + priceExtras));
            }
        });    
        
        extraBottumLeft.add(buttonTotal);  
        
        
        
        
        // Put frames together
        panelLeft.add(panelTopLeft);
        panelLeft.add(panelBottumLeft);
        panelLeft.add(extraBottumLeft);
        
        
        //===========================================================================================
        //Creating the panel at Center and adding components
        // CENTRAL TEXT SECTION
        JPanel panelCenter = new JPanel();
        panelCenter.setPreferredSize(new Dimension(300, 100));
        panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.PAGE_AXIS));
        panelCenter.setBackground(new Color(240, 200, 200));
        JLabel labelCentral = new JLabel("Text");
        labelCentral.setSize(100, 100);
        labelCentral.setFont(new Font("Courier New", Font.ITALIC, 24));
        labelCentral.setForeground(Color.black);
        panelCenter.add(labelCentral);
        taCentral = new JTextArea();
        JScrollPane scroll = new JScrollPane(taCentral);
        myFontSize = taCentral.getFont().deriveFont(Font.BOLD,20f);
        taCentral.setFont(myFontSize);
        taCentral.setEditable(true);
        taCentral.setLineWrap(true);
        taCentral.setWrapStyleWord(true);
        taCentral.setSize(100, 100);
        taCentral.setBackground(new Color(240, 200, 200));
        Border blackline = BorderFactory.createLineBorder(Color.black);
        taCentral.setBorder(blackline);
        panelCenter.add(scroll);


        //--------------------------------------------------------------------
        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.PAGE_END, panelBelow);
        frame.getContentPane().add(BorderLayout.PAGE_START, mb);
        frame.getContentPane().add(BorderLayout.LINE_START, panelLeft);
        frame.getContentPane().add(BorderLayout.LINE_END, panelRight);
        frame.getContentPane().add(BorderLayout.CENTER, panelCenter);
        frame.setVisible(true);
        
        
        bgL.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                int x = e.getX();
                int y = e.getY();
                String text = String.format("x: %d, y: %d", x, y);
                System.out.println(text);
                lblSign.setText(text);
            }
        });
        
        
        
    //======================================================================================    
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            TestGUI spaggie;
            spaggie = new TestGUI();
        });
    }
	
	
    
    // Second stratagy to check actions performed and a great way to test branch code coverage
    public void actionPerformed(ActionEvent e) {

    	System.out.println(e.getSource().getClass().toString());

    	if (e.getSource() instanceof javax.swing.JButton)
    	{
        	JButton jbu = (JButton) e.getSource();
        	if (jbu.getText().equals("Send"))
        	{
        		sendToTextArea(tfBelow.getText());
        	}
        	else if (jbu.getText().equals("Reset Text"))
        	{
        		clearTextfieldBelow();
        	}
        	else if (jbu.getText().equals("Reset TextArea"))
        	{
        		clearCentralTextArea();
        	}
        	else if (jbu.getText().equals("CHANGE BACKGROUND COLOR"))
        	{
        		toggleBackgroundColorCentralArea();        		
        	}
        	else if (jbu.getText().equals("PICTURE ON/OFF"))
        	{
        		toggleBackgroundPictureOnOffCentralArea();        		
        	}
        	else if (jbu.getText().equals("10 Table"))
        	{
        		tableOfTen();        		
        	}
        	else if (jbu.getText().equals("Print Y-table X times"))
        	{
        		String sX = tfXTimes.getText();
        		String sY = tfYTable.getText();
        		tablesY(sX, sY);        		
        	}
        	else if (jbu.getText().contentEquals("Make me Invisible"))
        	{
        		makeButtonInvisible(jbu);
        	}
 	
    	}
    	else if (e.getSource() instanceof javax.swing.JMenuItem)
    	{
    		JMenuItem jbu = (JMenuItem) e.getSource();
        	if (jbu.getText().equals("Open"))
        	{
        			open();        		
        	}
        	else if (jbu.getText().equals("Save as"))
        	{
        			saveAs();
        	}
        	else if (jbu.getText().equals("Info"))
        	{
        	    JOptionPane.showMessageDialog(this, info());
        	}
    	} 
    	
    	/*
    	Object source = e.getSource();
    	if (source instanceof JButton) {
    	    JButton button = (JButton) source;
    	    button.setText("Changed");
    	}
    	*/
	
    }
    
   
    //========================================================================================
    // METHODS FOR TEST PURPOSES
    
    private void writeToConsole(String text)
    {
    	System.out.println(text);
    }
    
    private void sendToTextArea(String centralText)
    {
    	taCentral.setText(centralText);
    }
    
    public void clearTextfieldBelow()
    {
    	tfBelow.setText("");
    }
    
    public void clearCentralTextArea()
    {
    	taCentral.setText("");
    }
    
    public void add()
    {
		String sNum1 = number1.getText();
		String sNum2 = number2.getText();
		try
		{
		Double dNum1 = Double.parseDouble(sNum1);
		Double dNum2 = Double.parseDouble(sNum2);
		Double dRes = dNum1 + dNum2;
		String sRes = dRes.toString();
		result.setText(sRes);
		}
		catch (NumberFormatException exc)
		{
    		sendToTextArea("Geldige waarde intypen");
    		result.setText("");
		}
    }
    
    public void substract()
    {
		String sNum1 = number1.getText();
		String sNum2 = number2.getText();
		try
		{
		Double dNum1 = Double.parseDouble(sNum1);
		Double dNum2 = Double.parseDouble(sNum2);
		Double dRes = dNum1 - dNum2;
		String sRes = dRes.toString();
		result.setText(sRes);
		}
		catch (NumberFormatException exc)
		{
			sendToTextArea("Geldige waarde intypen");
    		result.setText("");
		}
    }
    
    public void multiply()
    {
		String sNum1 = number1.getText();
		String sNum2 = number2.getText();
		try
		{
		Double dNum1 = Double.parseDouble(sNum1);
		Double dNum2 = Double.parseDouble(sNum2);
		Double dRes = dNum1 * dNum2;
		String sRes = dRes.toString();
		result.setText(sRes);
		}
		catch (NumberFormatException exc)
		{
    		sendToTextArea("Geldige waarde intypen");
    		result.setText("");
		}
    }
    
    public void devide()
    {
		String sNum1 = number1.getText();
		String sNum2 = number2.getText();
		try
		{
		Double dNum1 = Double.parseDouble(sNum1);
		Double dNum2 = Double.parseDouble(sNum2);
		Double dRes = dNum1 / dNum2;
		String sRes = dRes.toString();
		result.setText(sRes);
		}
		catch (NumberFormatException exc)
		{
    		sendToTextArea("Geldige waarde intypen");
    		result.setText("");
		}
    }
    
    public void modulus()
    {
		try
		{
		String sNum1 = number1.getText();
		String sNum2 = number2.getText();
		Double dNum1 = Double.parseDouble(sNum1);
		Double dNum2 = Double.parseDouble(sNum2);
		Double dRes = dNum1 % dNum2;
		String sRes = dRes.toString();
		result.setText(sRes);
		}
		catch (NumberFormatException exc)
		{
    		sendToTextArea("Geldige waarde intypen");
    		result.setText("");
		}
    }
    
    public void power()
    {
		try
		{    
		String sNum1 = number1.getText();
		String sNum2 = number2.getText();
		Double dNum1 = Double.parseDouble(sNum1);
		Double dNum2 = Double.parseDouble(sNum2);
		Double dRes = Math.pow(dNum1, dNum2);
		String sRes = dRes.toString();
		result.setText(sRes);
		}
		catch (NumberFormatException exc)
		{
    		sendToTextArea("Geldige waarde intypen");
    		result.setText("");
		}
    }
    
    public void root()
    {
		try
		{
		String sNum1 = number1.getText();
		Double dNum1 = Double.parseDouble(sNum1);
		Double dRes = Math.sqrt(dNum1);
		String sRes = dRes.toString();
		result.setText(sRes);
		}
		catch (NumberFormatException exc)
		{
    		sendToTextArea("Geldige waarde intypen");
    		result.setText("");
		}
    }
    
    public void clear()
    {
		number1.setText("");
		number2.setText("");
		result.setText("");
    }
    
    public void average()
    {
		String sNum1 = number1.getText();
		String sNum2 = number2.getText();
		Double dNum1 = 0.0;
		Double dNum2 = 0.0;
		try
		{
			dNum1 = Double.parseDouble(sNum1);
			dNum2 = Double.parseDouble(sNum2);
		}
		catch (NumberFormatException exc)
		{
    		sendToTextArea("Geldige waarde intypen");
    		result.setText("");
		}
		Double dRes = (dNum1 + dNum2) / 2;
		String sRes = dRes.toString();
		result.setText(sRes);
    }
    
    public void toggleBackgroundColorCentralArea()
    {
    	if (boolSwapper)
    	{
    		taCentral.setBackground(Color.cyan);
    	}
    	else
    	{
    		taCentral.setBackground(new Color(240, 200, 200));
    	}
    	boolSwapper = !boolSwapper;
    }
    
    public void toggleBackgroundPictureOnOffCentralArea()
    {
    	

    	if (boolPictureSwapper == false)
    	{
    	    bgL.setVisible(false);
    		boolPictureSwapper = true;
    	}
    	else
    	{
    		bgL.setVisible(true);
    		boolPictureSwapper = false;
    	}
    	repaint();
    }
    
    
    public void willNeverBeExecuted()
    {
    	int count = 0;
    	while (count<1000)
    	{
    		System.out.println("This method will never be called!");
    		count++;
    	}
    }
    
    public void tableOfTen()
    {
    	String result = "";
    	for (int i = 0; i < 31; i++)
    	{
    		result += i + " * 10 = " + (i * 10) + "\n";
    	}
    	sendToTextArea(result);
    }
    
    public void tablesY(String sX, String sY)
    {
    	int x = 0;
    	int y = 0;;
    	System.out.println(sX);
    	try
    	{
    		x = Integer.parseInt(sX);
        	System.out.println(x);
    		y = Integer.parseInt(sY);
    	}
    	catch (NumberFormatException exc)
    	{
    		sendToTextArea("Geldige waarde intypen");
    		System.out.println(exc.toString());
    		return;
    	}
        if (x > 999999)
        {
            x = 999;
        }
        else if (x > 99999)
        {
            x /= 1000;
        }
         else if (x > 9999)
        {
            x /= 100;
        }
         else if (x > 999)
        {
            x /= 10;
        }
         else if (x > 999)
        {
            x /= 10; // programs cannot come here
        }
        tfXTimes.setText(String.valueOf(x));
    	String result = "";
    	int i = 0;
    	while (i <= x)
    	{
    		result += i + " * " + y + " = " + (i * y) + "\n";
    		i++;
    	}
     	System.out.println(result);
    	sendToTextArea(result);
    }
    
    public void makeButtonInvisible(JButton jButton)
    {
    	numInvisibleClicks++;
    	lblSign.setText("" + numInvisibleClicks);
    	jButton.setVisible(false);
    }
    
    public void saveAs()
    {
		System.out.println("Save bestand");
		JFileChooser save = new JFileChooser();

	      int option = save.showSaveDialog(TestGUI.this);
	      if (option == JFileChooser.APPROVE_OPTION) {
				try {
					// bufferedwriter usig File writer 
					BufferedWriter bw=new BufferedWriter(new FileWriter(save.getSelectedFile().getPath()));  
					bw.write(taCentral.getText());
					bw.close();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	      }
	      if (option == JFileChooser.CANCEL_OPTION) {
	        filename.setText("You pressed cancel");
	        dir.setText("");
	      }
    }
    
    public void open()
    {
		System.out.println("Open bestand");
	    JFileChooser choose = new JFileChooser();
	     int option = choose.showOpenDialog(TestGUI.this);
	     if (option == JFileChooser.APPROVE_OPTION) {
	    	 this.taCentral.setText("");
			 try 
			 {
					Scanner scan=new Scanner(new FileReader(choose.getSelectedFile().getPath()));  
					//scanner to read lines in your text file, a stream FileReader to read the text file
					while(scan.hasNext()){
						taCentral.append(scan.nextLine()+"\n");
					}
  		   	 } catch (FileNotFoundException e1) 
			 {
					e1.printStackTrace();
			 }
	      }
	      if (option == JFileChooser.CANCEL_OPTION) {
	        filename.setText("You pressed cancel");
	        dir.setText("");
	      }
    }
    
    public String info()
    {
		String info;
		info = "Program to test TESTAR. \nWith a lot of basic programming, that is part of the standard work of a programmer.\n"
				+ "This spaghetti of code is a good basis to create mutants, and test the behavior of a TESTAR Oracle on standard code.\n Aaron van der Brugge.";
		return info;

    }
    
    public void writeName(String name)
    {
    	System.out.println("de naam is " + name);
    	if (name==null) return;
    	if (!name.equals(""))
    	{
    		taCentral.setText("Welcome, my dear " + name);
    	}
    }
  
    public JRadioButton makeRadioButton(String name, boolean status, float price)
    {
       JRadioButton radioButton = new JRadioButton(name, status);
        radioButton.addItemListener(new ItemListener() {
           public void itemStateChanged(ItemEvent e) { 
        	   int state = e.getStateChange();
               if (state == ItemEvent.SELECTED) {
            	   fPizzaPrice = price;
               } else if (state == ItemEvent.DESELECTED) {
            	   fPizzaPrice = 0;
               }
           }           
       });

       return radioButton;
    }
    
    
  
    // Listens to the check boxes.
    public void itemStateChanged(ItemEvent e) {

       Object source = e.getItemSelectable();
        if (source == standardButton) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                price1 = 1;
            }
            else price1 = 0;
        } else if (source == saladButton) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                price2 = 2;
            }
            else price2 = 0;
        } else if (source == grandeButton) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                price3 = 3;
            }
            else price3 = 0;
        } 
        priceExtras = price1+price2+price3;
        taCentral.setText("Extras costs: " + priceExtras);

    }

    

} // End class
