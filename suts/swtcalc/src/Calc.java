
import java.io.IOException;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Calc {

	static int crashCount = 0;
	static int freezeCount = 0;
	static int shutdownCount = 0;
	Calculator calc = new Calculator();

	public void createComponents() {
		FillLayout2 = new FillLayout();
		Display2 = new Display();
		Calculator = new Shell(Display2, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		Calculator.setBounds(0, 0, 600, 600);
		Calculator.setText("Calculator");
		Calculator.setLayout(FillLayout2);
		menuBar1 = new Menu(Calculator , SWT.BAR);
		Calculator.setMenuBar(menuBar1);

		FileMenuItem = new MenuItem(menuBar1, SWT.CASCADE);
		FileMenuItem.setText("File");
		FileMenu = new Menu(Calculator , SWT.DROP_DOWN);
		FileMenuItem.setMenu(FileMenu);
		OpenMenuItem = new MenuItem(FileMenu, SWT.PUSH);
		OpenMenuItem.setText("Open File...");

		//simulate file open dialog
		OpenMenuItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				FileDialog fd = new FileDialog(Calculator, SWT.OPEN);
				fd.open();
			}
		});

		ShutDownMenuItem = new MenuItem(FileMenu, SWT.PUSH);
		ShutDownMenuItem.setText("Shut Down...");


		//shut down
		ShutDownMenuItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Calculator.close();
			}
		});


		Menu4 = new Menu(Calculator , SWT.DROP_DOWN);
		MenuItem4 = new MenuItem(menuBar1 , SWT.CASCADE);
		MenuItem4.setText("Edit");
		MenuItem4.setMenu(Menu4);
		MenuItem5 = new MenuItem(Menu4 , SWT.PUSH);
		MenuItem5.setText("Copy");
		MenuItem6 = new MenuItem(Menu4 , SWT.PUSH);
		MenuItem6.setText("Paste");


		//hide an easter egg
		MenuItem5.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				MessageBox mb = new MessageBox(Calculator, SWT.ICON_ERROR);
				mb.setText("Oh oh oh");
				mb.setMessage("Found an issue!");
				mb.open();
			}
		});


		//simulate critical message box
		MenuItem6.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				MessageBox mb = new MessageBox(Calculator, SWT.ICON_ERROR);
				mb.setText("NullPointerException");
				mb.setMessage("Warning: A critical Error occurred!");
				mb.open();
			}
		});


		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 80;
		gridData.minimumHeight = 180;


		Menu7 = new Menu(Calculator , SWT.DROP_DOWN);
		MenuItem7 = new MenuItem(menuBar1 , SWT.CASCADE);
		MenuItem7.setText("View");
		MenuItem7.setMenu(Menu7);
		MenuItem8 = new MenuItem(Menu7 , SWT.PUSH);
		MenuItem8.setText("Standard");
		MenuItem8.setEnabled(false);
		MenuItem9 = new MenuItem(Menu7 , SWT.PUSH);
		MenuItem9.setText("Scientific");
		MenuItem10 = new MenuItem(Menu7 , SWT.PUSH);
		MenuItem10.setText("Digital Grouping");


		Menu11 = new Menu(Calculator , SWT.DROP_DOWN);
		MenuItem11 = new MenuItem(menuBar1 , SWT.CASCADE);
		MenuItem11.setText("Help");
		MenuItem11.setMenu(Menu11);
		MenuItem12 = new MenuItem(Menu11 , SWT.PUSH);
		MenuItem12.setText("Open MS Paint");
		MenuItem13 = new MenuItem(Menu11 , SWT.PUSH);
		MenuItem13.setText("About Calculator");
		MenuItem14 = new MenuItem(Menu11 , SWT.PUSH);
		MenuItem14.setText("Contact");




		//Division by 0
		MenuItem9.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int x;
				x = 10/0;
			}
		});




		//NullpointerException
		MenuItem10.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				MessageBox mb = null;
				mb.setText("null");
				mb.open();
			}
		});


		// startup external process
		MenuItem12.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){

				try {
					Process p = Runtime.getRuntime().exec(System.getenv("windir") +"/system32/mspaint.exe");
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});




		//About the calculator information
		MenuItem13.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				MessageBox mb = new MessageBox(Calculator, SWT.ICON_INFORMATION);
				mb.setText("About");
				mb.setMessage("This calculator has not been written, and should better not be used, for doing calculation. This buggy calculator has been written for the purpose of illustrating and exercising with TESTAR at practical or DIY hansdon sessions.");
				mb.open();
			}
		});


		//hide an TESTAR message
		MenuItem14.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				MessageBox mb = new MessageBox(Calculator, SWT.ICON_INFORMATION);
				mb.setText("OH YES!!");
				mb.setMessage("TESTAR is awesome! Go to www.testar.org");
				mb.open();
			}
		});

		Composite14 = new Composite(Calculator , SWT.FLAT);
		RowLayout15 = new RowLayout(SWT.VERTICAL);
		Composite14.setLayout(RowLayout15);
		Composite16 = new Composite(Composite14 , SWT.FLAT);
		//Text17 = new Text(Composite16 , SWT.BORDER);
		Text17 = new Label(Composite16 , SWT.BORDER); // by urueda
		Text17.setBounds(1 , 2 , 170 , 20);
		Text17.setText("0");
		Text17.setOrientation(SWT.LEFT_TO_RIGHT);
		Composite18 = new Composite(Composite14 , SWT.FLAT);
		GridLayout19 = new GridLayout();
		Composite18.setLayout(GridLayout19);
		GridLayout19.numColumns = 4;
		GridLayout19.horizontalSpacing = 16;
		Button20 = new Button(Composite18 , SWT.PUSH);
		Button20.setText("");
		Button21 = new Button(Composite18 , SWT.PUSH);
		Button21.setText("Backspace");
		Button22 = new Button(Composite18 , SWT.PUSH);
		Button22.setText("CE");
		Button23 = new Button(Composite18 , SWT.PUSH);
		Button23.setText("C");


		Button20.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText("What are you doing!!?????");
			}
		});



		Button21.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText("%&$%&$%TFCGBCGHRTE$TRED$%$%$");
			}
		});


		Button23.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				//calc.hitKey(Calculator.Clear);
				Text17.setText("0");
			}
		});


		JCalcPanel = new Composite(Composite14 , SWT.FLAT);
		GridLayout25 = new GridLayout();
		GridLayout25.makeColumnsEqualWidth = true;

		JCalcPanel.setLayout(GridLayout25);
		GridLayout25.numColumns = 6;
		GridLayout25.horizontalSpacing = 4;
		Button26 = new Button(JCalcPanel , SWT.PUSH);
		Button26.setText("log x");
		Button26.setLayoutData(gridData);


		//simulate a crash
		Button26.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				crashCount++;
				if(crashCount == 3)
					throw new RuntimeException("Unknown Error!");
			}
		});


		Button27 = new Button(JCalcPanel , SWT.PUSH);
		Button27.setText("7");
		Button27.setLayoutData(gridData);
		Button27.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "7");
			}
		});




		Button28 = new Button(JCalcPanel , SWT.PUSH);
		Button28.setText("8");
		Button28.setLayoutData(gridData);
		Button28.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "8");
			}
		});


		Button29 = new Button(JCalcPanel , SWT.PUSH);
		Button29.setText("9");
		Button29.setLayoutData(gridData);
		Button29.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "9");
			}
		});

		Button30 = new Button(JCalcPanel , SWT.PUSH);
		Button30.setText("/");
		Button30.setLayoutData(gridData);
		Button31 = new Button(JCalcPanel , SWT.PUSH);
		Button31.setText("sqrt");
		Button31.setLayoutData(gridData);

		Button31.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				String txt = Text17.getText();
				try{
					Double d = Double.parseDouble(txt);
					Text17.setText(Double.toString(Math.sqrt(d)));
				}catch(Throwable t){
					MessageBox mb = new MessageBox(Calculator, SWT.ICON_ERROR);
					mb.setText(t.getMessage());
					mb.setMessage(t.toString());
					mb.open();

				}
			}
		});



		Button32 = new Button(JCalcPanel , SWT.PUSH);
		Button32.setText("tan x");
		Button32.setLayoutData(gridData);

		//simulate a freeze
		Button32.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				freezeCount++;
				if(freezeCount == 3)
					while(true);
			}
		});


		Button33 = new Button(JCalcPanel , SWT.PUSH);
		Button33.setText("4");
		Button33.setLayoutData(gridData);
		Button33.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "4");
			}
		});

		Button34 = new Button(JCalcPanel , SWT.PUSH);
		Button34.setText("5");
		Button34.setLayoutData(gridData);
		Button34.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				//Text17.setText(Text17.getText() + "5");
				// start by urueda
				if (new java.util.Random(System.currentTimeMillis()).nextInt() > Integer.MAX_VALUE / 2)
					Text17.setText(Text17.getText()); // + "NOT_FIVE");
				else
					Text17.setText(Text17.getText() + "5");
				// end by urueda
			}
		});

		Button35 = new Button(JCalcPanel , SWT.PUSH);
		Button35.setText("6");
		Button35.setLayoutData(gridData);
		Button35.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "6");
			}
		});

		Button36 = new Button(JCalcPanel , SWT.PUSH);
		Button36.setText("*");
		Button36.setLayoutData(gridData);
		Button36.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "*");
			}
		});

		Button37 = new Button(JCalcPanel , SWT.PUSH);
		Button37.setText("%");
		Button37.setLayoutData(gridData);
		Button38 = new Button(JCalcPanel , SWT.PUSH);
		Button38.setText("MS");
		Button38.setLayoutData(gridData);
		Button39 = new Button(JCalcPanel , SWT.PUSH);
		Button39.setText("1");
		Button39.setLayoutData(gridData);

		Button39.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "1");
			}
		});


		Button40 = new Button(JCalcPanel , SWT.PUSH);
		Button40.setText("2");
		Button40.setLayoutData(gridData);
		Button40.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "2");
			}
		});



		Button41 = new Button(JCalcPanel , SWT.PUSH);
		Button41.setText("3");
		Button41.setLayoutData(gridData);

		Button41.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "3");
			}
		});


		Button42 = new Button(JCalcPanel , SWT.PUSH);
		Button42.setText("-");
		Button42.setLayoutData(gridData);
		Button43 = new Button(JCalcPanel , SWT.PUSH);
		Button43.setText("1/x");
		Button43.setLayoutData(gridData);

		Button43.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){

				Double d = Double.parseDouble(Text17.getText());
				Text17.setText(Double.toString(1/d));
			}
		});

		Button44 = new Button(JCalcPanel , SWT.PUSH);
		Button44.setText("M+");
		Button44.setLayoutData(gridData);
		Button45 = new Button(JCalcPanel , SWT.PUSH);
		Button45.setText("0");
		Button45.setLayoutData(gridData);

		Button45.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e){
				Text17.setText(Text17.getText() + "0");
			}
		});


		Button46 = new Button(JCalcPanel , SWT.PUSH);
		Button46.setText("+/-");
		Button46.setLayoutData(gridData);
		Button47 = new Button(JCalcPanel , SWT.PUSH);
		Button47.setText(".");
		Button47.setLayoutData(gridData);
		Button48 = new Button(JCalcPanel , SWT.PUSH);
		Button48.setText("+");
		Button48.setLayoutData(gridData);
		Button49 = new Button(JCalcPanel , SWT.PUSH);
		Button49.setText("=");
		Button49.setLayoutData(gridData);


		//simulate critical message box
		Button49.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				MessageBox mb = new MessageBox(Calculator, SWT.ICON_ERROR);
				mb.setText("Strange error");
				mb.setMessage("Whatever you are trying to calculate. I cannot do that.");
				mb.open();
			}
		});


		Calculator.pack();
		Calculator.open();
		while (!Calculator.isDisposed ()) {
			if (!Display2.readAndDispatch ())
				Display2.sleep ();
		}
		Display2.dispose ();
	}

	public static Calc getInstance() {
		if ( gui_instance == null ) {
			gui_instance = new Calc();
			gui_instance.createComponents();
		}
		return gui_instance;
	}

	public static void main(String[] args) {
		getInstance();
	}

	protected Button Button40;
	protected Button Button41;
	protected Shell Calculator;
	protected FillLayout FillLayout2;
	protected GridLayout GridLayout19;
	protected MenuItem MenuItem8;
	protected MenuItem MenuItem11;
	protected MenuItem MenuItem4;
	protected MenuItem MenuItem14;
	protected GridLayout GridLayout25;
	protected MenuItem MenuItem7, FileMenuItem, OpenMenuItem, ShutDownMenuItem;
	protected Menu FileMenu;
	protected RowLayout RowLayout15;
	protected MenuItem MenuItem10;
	protected Menu Menu11;
	protected Composite JCalcPanel;
	protected Display Display2;
	protected Menu Menu4;
	protected MenuItem MenuItem13;
	protected MenuItem MenuItem6;
	protected Composite Composite18;
	protected Composite Composite16;
	protected Label Text17;
	protected Menu menuBar1;
	protected Button Button31;
	protected Button Button32;
	protected Button Button33;
	protected Button Button34;
	protected Button Button35;
	protected Button Button36;
	protected Button Button37;
	protected Button Button38;
	protected Composite Composite14;
	protected Menu Menu7;
	protected Button Button39;
	protected MenuItem MenuItem12;
	protected MenuItem MenuItem5;
	protected Button Button49;
	protected Button Button48;
	protected Button Button47;
	protected Button Button46;
	protected Button Button45;
	protected Button Button20;
	protected Button Button21;
	protected Button Button22;
	protected Button Button23;
	protected Button Button26;
	protected Button Button27;
	protected Button Button42;
	protected Button Button43;
	protected Button Button44;
	protected Button Button30;
	private static Calc gui_instance;
	protected Button Button28;
	protected Button Button29;
	protected MenuItem MenuItem9;
}