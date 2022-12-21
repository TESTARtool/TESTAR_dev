namespace BuggyProgram;

partial class MainForm
{
	/// <summary>
	///  Required designer variable.
	/// </summary>
	private System.ComponentModel.IContainer components = null;

	/// <summary>
	///  Clean up any resources being used.
	/// </summary>
	/// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
	protected override void Dispose(bool disposing)
	{
		if (disposing && (components != null))
		{
			components.Dispose();
		}
		base.Dispose(disposing);
	}

	#region Windows Form Designer generated code

	/// <summary>
	///  Required method for Designer support - do not modify
	///  the contents of this method with the code editor.
	/// </summary>
	private void InitializeComponent()
	{
			this.panel1 = new System.Windows.Forms.Panel();
			this.WrongAncorsTopButton = new System.Windows.Forms.Button();
			this.panel2 = new System.Windows.Forms.Panel();
			this.ShowSettingsFormButton = new System.Windows.Forms.Button();
			this.button2 = new System.Windows.Forms.Button();
			this.button1 = new System.Windows.Forms.Button();
			this.WrongAncorsBottomButton = new System.Windows.Forms.Button();
			this.panel3 = new System.Windows.Forms.Panel();
			this.textBox2 = new System.Windows.Forms.TextBox();
			this.textBox1 = new System.Windows.Forms.TextBox();
			this.listBox2 = new System.Windows.Forms.ListBox();
			this.listBox1 = new System.Windows.Forms.ListBox();
			this.checkBox2 = new System.Windows.Forms.CheckBox();
			this.checkBox1 = new System.Windows.Forms.CheckBox();
			this.SearchFormButton = new System.Windows.Forms.Button();
			this.panel1.SuspendLayout();
			this.panel2.SuspendLayout();
			this.panel3.SuspendLayout();
			this.SuspendLayout();
			// 
			// panel1
			// 
			this.panel1.Controls.Add(this.WrongAncorsTopButton);
			this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
			this.panel1.Location = new System.Drawing.Point(0, 0);
			this.panel1.Name = "panel1";
			this.panel1.Size = new System.Drawing.Size(800, 48);
			this.panel1.TabIndex = 0;
			// 
			// WrongAncorsTopButton
			// 
			this.WrongAncorsTopButton.Location = new System.Drawing.Point(639, 12);
			this.WrongAncorsTopButton.Name = "WrongAncorsTopButton";
			this.WrongAncorsTopButton.Size = new System.Drawing.Size(150, 26);
			this.WrongAncorsTopButton.TabIndex = 0;
			this.WrongAncorsTopButton.Text = "WrongAncorsTop";
			this.WrongAncorsTopButton.UseVisualStyleBackColor = true;
			this.WrongAncorsTopButton.Click += new System.EventHandler(this.WrongAncorsTopButton_Click);
			// 
			// panel2
			// 
			this.panel2.Controls.Add(this.SearchFormButton);
			this.panel2.Controls.Add(this.ShowSettingsFormButton);
			this.panel2.Controls.Add(this.button2);
			this.panel2.Controls.Add(this.button1);
			this.panel2.Controls.Add(this.WrongAncorsBottomButton);
			this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
			this.panel2.Location = new System.Drawing.Point(0, 379);
			this.panel2.Name = "panel2";
			this.panel2.Size = new System.Drawing.Size(800, 71);
			this.panel2.TabIndex = 1;
			// 
			// ShowSettingsFormButton
			// 
			this.ShowSettingsFormButton.Location = new System.Drawing.Point(177, 11);
			this.ShowSettingsFormButton.Name = "ShowSettingsFormButton";
			this.ShowSettingsFormButton.Size = new System.Drawing.Size(139, 48);
			this.ShowSettingsFormButton.TabIndex = 3;
			this.ShowSettingsFormButton.Text = "Settings form";
			this.ShowSettingsFormButton.UseVisualStyleBackColor = true;
			this.ShowSettingsFormButton.Click += new System.EventHandler(this.ShowInputFormButton_Click);
			// 
			// button2
			// 
			this.button2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.button2.Location = new System.Drawing.Point(13, 11);
			this.button2.Name = "button2";
			this.button2.Size = new System.Drawing.Size(149, 48);
			this.button2.TabIndex = 2;
			this.button2.Text = "Dummy button";
			this.button2.UseVisualStyleBackColor = true;
			this.button2.Click += new System.EventHandler(this.button2_Click);
			// 
			// button1
			// 
			this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.button1.Location = new System.Drawing.Point(484, 11);
			this.button1.Name = "button1";
			this.button1.Size = new System.Drawing.Size(149, 48);
			this.button1.TabIndex = 1;
			this.button1.Text = "CorrectAncorsBottom";
			this.button1.UseVisualStyleBackColor = true;
			this.button1.Click += new System.EventHandler(this.button1_Click);
			// 
			// WrongAncorsBottomButton
			// 
			this.WrongAncorsBottomButton.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left)));
			this.WrongAncorsBottomButton.Location = new System.Drawing.Point(639, 11);
			this.WrongAncorsBottomButton.Name = "WrongAncorsBottomButton";
			this.WrongAncorsBottomButton.Size = new System.Drawing.Size(149, 48);
			this.WrongAncorsBottomButton.TabIndex = 0;
			this.WrongAncorsBottomButton.Text = "WrongAncorsBottom";
			this.WrongAncorsBottomButton.UseVisualStyleBackColor = true;
			this.WrongAncorsBottomButton.Click += new System.EventHandler(this.WrongAncorsBottomButton_Click);
			// 
			// panel3
			// 
			this.panel3.Controls.Add(this.textBox2);
			this.panel3.Controls.Add(this.textBox1);
			this.panel3.Controls.Add(this.listBox2);
			this.panel3.Controls.Add(this.listBox1);
			this.panel3.Controls.Add(this.checkBox2);
			this.panel3.Controls.Add(this.checkBox1);
			this.panel3.Dock = System.Windows.Forms.DockStyle.Fill;
			this.panel3.Location = new System.Drawing.Point(0, 48);
			this.panel3.Name = "panel3";
			this.panel3.Size = new System.Drawing.Size(800, 331);
			this.panel3.TabIndex = 2;
			// 
			// textBox2
			// 
			this.textBox2.Location = new System.Drawing.Point(460, 80);
			this.textBox2.Name = "textBox2";
			this.textBox2.Size = new System.Drawing.Size(246, 23);
			this.textBox2.TabIndex = 5;
			this.textBox2.Text = "this textbox is on top of another textbox";
			// 
			// textBox1
			// 
			this.textBox1.Location = new System.Drawing.Point(442, 69);
			this.textBox1.Name = "textBox1";
			this.textBox1.Size = new System.Drawing.Size(200, 23);
			this.textBox1.TabIndex = 4;
			// 
			// listBox2
			// 
			this.listBox2.FormattingEnabled = true;
			this.listBox2.ItemHeight = 15;
			this.listBox2.Items.AddRange(new object[] {
            "0",
            "1",
            "A",
            "B",
            "C",
            "Z"});
			this.listBox2.Location = new System.Drawing.Point(229, 98);
			this.listBox2.Name = "listBox2";
			this.listBox2.Size = new System.Drawing.Size(139, 184);
			this.listBox2.TabIndex = 3;
			// 
			// listBox1
			// 
			this.listBox1.FormattingEnabled = true;
			this.listBox1.ItemHeight = 15;
			this.listBox1.Items.AddRange(new object[] {
            "This",
            "Is",
            "A",
            "List",
            "With",
            "Unsorted",
            "Listitems"});
			this.listBox1.Location = new System.Drawing.Point(32, 98);
			this.listBox1.Name = "listBox1";
			this.listBox1.Size = new System.Drawing.Size(139, 184);
			this.listBox1.TabIndex = 2;
			// 
			// checkBox2
			// 
			this.checkBox2.AutoSize = true;
			this.checkBox2.Location = new System.Drawing.Point(29, 6);
			this.checkBox2.Name = "checkBox2";
			this.checkBox2.Size = new System.Drawing.Size(133, 19);
			this.checkBox2.TabIndex = 1;
			this.checkBox2.Text = "Checkbox with label";
			this.checkBox2.UseVisualStyleBackColor = true;
			// 
			// checkBox1
			// 
			this.checkBox1.AutoSize = true;
			this.checkBox1.Location = new System.Drawing.Point(29, 30);
			this.checkBox1.Name = "checkBox1";
			this.checkBox1.Size = new System.Drawing.Size(15, 14);
			this.checkBox1.TabIndex = 0;
			this.checkBox1.UseVisualStyleBackColor = true;
			// 
			// SearchFormButton
			// 
			this.SearchFormButton.Location = new System.Drawing.Point(331, 11);
			this.SearchFormButton.Name = "SearchFormButton";
			this.SearchFormButton.Size = new System.Drawing.Size(139, 48);
			this.SearchFormButton.TabIndex = 4;
			this.SearchFormButton.Text = "Search form";
			this.SearchFormButton.UseVisualStyleBackColor = true;
			this.SearchFormButton.Click += new System.EventHandler(this.SearchFormButton_Click);
			// 
			// MainForm
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 15F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(800, 450);
			this.Controls.Add(this.panel3);
			this.Controls.Add(this.panel2);
			this.Controls.Add(this.panel1);
			this.Name = "MainForm";
			this.panel1.ResumeLayout(false);
			this.panel2.ResumeLayout(false);
			this.panel3.ResumeLayout(false);
			this.panel3.PerformLayout();
			this.ResumeLayout(false);

	}

	#endregion

	private Panel panel1;
	private Button WrongAncorsTopButton;
	private Panel panel2;
	private Button WrongAncorsBottomButton;
	private Panel panel3;
	private Button button1;
	private CheckBox checkBox2;
	private CheckBox checkBox1;
	private Button button2;
	private TextBox textBox2;
	private TextBox textBox1;
	private ListBox listBox2;
	private ListBox listBox1;
	private Button ShowSettingsFormButton;
	private Button SearchFormButton;
}
