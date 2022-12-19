namespace BuggyProgram;

partial class SettingsForm
{
	/// <summary>
	/// Required designer variable.
	/// </summary>
	private System.ComponentModel.IContainer components = null;

	/// <summary>
	/// Clean up any resources being used.
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
	/// Required method for Designer support - do not modify
	/// the contents of this method with the code editor.
	/// </summary>
	private void InitializeComponent()
	{
			this.panel1 = new System.Windows.Forms.Panel();
			this.SaveAndCloseButton = new System.Windows.Forms.Button();
			this.SaveButton = new System.Windows.Forms.Button();
			this.panel2 = new System.Windows.Forms.Panel();
			this.label3 = new System.Windows.Forms.Label();
			this.ChosenValueResetToZeroCombobox = new System.Windows.Forms.ComboBox();
			this.groupBox1 = new System.Windows.Forms.GroupBox();
			this.CannotSelectOption = new System.Windows.Forms.RadioButton();
			this.AlwaysCheckedOption = new System.Windows.Forms.RadioButton();
			this.label2 = new System.Windows.Forms.Label();
			this.ValueDoesNotSaveCombobox = new System.Windows.Forms.ComboBox();
			this.label1 = new System.Windows.Forms.Label();
			this.ValueDoesSaveCombobox = new System.Windows.Forms.ComboBox();
			this.panel1.SuspendLayout();
			this.panel2.SuspendLayout();
			this.groupBox1.SuspendLayout();
			this.SuspendLayout();
			// 
			// panel1
			// 
			this.panel1.Controls.Add(this.SaveAndCloseButton);
			this.panel1.Controls.Add(this.SaveButton);
			this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
			this.panel1.Location = new System.Drawing.Point(0, 0);
			this.panel1.Name = "panel1";
			this.panel1.Size = new System.Drawing.Size(800, 56);
			this.panel1.TabIndex = 0;
			// 
			// SaveAndCloseButton
			// 
			this.SaveAndCloseButton.Location = new System.Drawing.Point(185, 10);
			this.SaveAndCloseButton.Name = "SaveAndCloseButton";
			this.SaveAndCloseButton.Size = new System.Drawing.Size(156, 35);
			this.SaveAndCloseButton.TabIndex = 1;
			this.SaveAndCloseButton.Text = "Save and close";
			this.SaveAndCloseButton.UseVisualStyleBackColor = true;
			this.SaveAndCloseButton.Click += new System.EventHandler(this.SaveAndCloseButton_Click);
			// 
			// SaveButton
			// 
			this.SaveButton.Location = new System.Drawing.Point(20, 10);
			this.SaveButton.Name = "SaveButton";
			this.SaveButton.Size = new System.Drawing.Size(156, 35);
			this.SaveButton.TabIndex = 0;
			this.SaveButton.Text = "Save";
			this.SaveButton.UseVisualStyleBackColor = true;
			this.SaveButton.Click += new System.EventHandler(this.SaveButton_Click);
			// 
			// panel2
			// 
			this.panel2.Controls.Add(this.label3);
			this.panel2.Controls.Add(this.ChosenValueResetToZeroCombobox);
			this.panel2.Controls.Add(this.groupBox1);
			this.panel2.Controls.Add(this.label2);
			this.panel2.Controls.Add(this.ValueDoesNotSaveCombobox);
			this.panel2.Controls.Add(this.label1);
			this.panel2.Controls.Add(this.ValueDoesSaveCombobox);
			this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
			this.panel2.Location = new System.Drawing.Point(0, 56);
			this.panel2.Name = "panel2";
			this.panel2.Size = new System.Drawing.Size(800, 394);
			this.panel2.TabIndex = 1;
			this.panel2.Paint += new System.Windows.Forms.PaintEventHandler(this.panel2_Paint);
			// 
			// label3
			// 
			this.label3.AutoSize = true;
			this.label3.Location = new System.Drawing.Point(21, 101);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(143, 15);
			this.label3.TabIndex = 7;
			this.label3.Text = "Chosen value will be reset";
			// 
			// ChosenValueResetToZeroCombobox
			// 
			this.ChosenValueResetToZeroCombobox.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
			this.ChosenValueResetToZeroCombobox.FormattingEnabled = true;
			this.ChosenValueResetToZeroCombobox.Items.AddRange(new object[] {
            "Value 1",
            "Value 2",
            "Value 3"});
			this.ChosenValueResetToZeroCombobox.Location = new System.Drawing.Point(220, 98);
			this.ChosenValueResetToZeroCombobox.Name = "ChosenValueResetToZeroCombobox";
			this.ChosenValueResetToZeroCombobox.Size = new System.Drawing.Size(121, 23);
			this.ChosenValueResetToZeroCombobox.TabIndex = 6;
			this.ChosenValueResetToZeroCombobox.SelectedIndexChanged += new System.EventHandler(this.comboBox1_SelectedIndexChanged);
			// 
			// groupBox1
			// 
			this.groupBox1.Controls.Add(this.CannotSelectOption);
			this.groupBox1.Controls.Add(this.AlwaysCheckedOption);
			this.groupBox1.Location = new System.Drawing.Point(20, 132);
			this.groupBox1.Name = "groupBox1";
			this.groupBox1.Size = new System.Drawing.Size(321, 76);
			this.groupBox1.TabIndex = 5;
			this.groupBox1.TabStop = false;
			this.groupBox1.Text = "Options";
			// 
			// CannotSelectOption
			// 
			this.CannotSelectOption.AutoSize = true;
			this.CannotSelectOption.Location = new System.Drawing.Point(15, 47);
			this.CannotSelectOption.Name = "CannotSelectOption";
			this.CannotSelectOption.Size = new System.Drawing.Size(222, 19);
			this.CannotSelectOption.TabIndex = 1;
			this.CannotSelectOption.Text = "Not (but should be) selectable option";
			this.CannotSelectOption.UseVisualStyleBackColor = true;
			this.CannotSelectOption.CheckedChanged += new System.EventHandler(this.CannotSelectOption_CheckedChanged);
			// 
			// AlwaysCheckedOption
			// 
			this.AlwaysCheckedOption.AutoSize = true;
			this.AlwaysCheckedOption.Checked = true;
			this.AlwaysCheckedOption.Location = new System.Drawing.Point(15, 22);
			this.AlwaysCheckedOption.Name = "AlwaysCheckedOption";
			this.AlwaysCheckedOption.Size = new System.Drawing.Size(210, 19);
			this.AlwaysCheckedOption.TabIndex = 0;
			this.AlwaysCheckedOption.TabStop = true;
			this.AlwaysCheckedOption.Text = "This options will always be selected";
			this.AlwaysCheckedOption.UseVisualStyleBackColor = true;
			// 
			// label2
			// 
			this.label2.AutoSize = true;
			this.label2.Location = new System.Drawing.Point(21, 62);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(169, 15);
			this.label2.TabIndex = 4;
			this.label2.Text = "Chosen value will not be saved";
			// 
			// ValueDoesNotSaveCombobox
			// 
			this.ValueDoesNotSaveCombobox.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
			this.ValueDoesNotSaveCombobox.FormattingEnabled = true;
			this.ValueDoesNotSaveCombobox.Items.AddRange(new object[] {
            "Value 1",
            "Value 2",
            "Value 3"});
			this.ValueDoesNotSaveCombobox.Location = new System.Drawing.Point(220, 59);
			this.ValueDoesNotSaveCombobox.Name = "ValueDoesNotSaveCombobox";
			this.ValueDoesNotSaveCombobox.Size = new System.Drawing.Size(121, 23);
			this.ValueDoesNotSaveCombobox.TabIndex = 3;
			this.ValueDoesNotSaveCombobox.SelectionChangeCommitted += new System.EventHandler(this.ValueDoesNotSaveCombobox_SelectionChangeCommitted);
			// 
			// label1
			// 
			this.label1.AutoSize = true;
			this.label1.Location = new System.Drawing.Point(21, 22);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(193, 15);
			this.label1.TabIndex = 2;
			this.label1.Text = "Chosen value will be saved corectly";
			// 
			// ValueDoesSaveCombobox
			// 
			this.ValueDoesSaveCombobox.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
			this.ValueDoesSaveCombobox.FormattingEnabled = true;
			this.ValueDoesSaveCombobox.Items.AddRange(new object[] {
            "Value 1",
            "Value 2",
            "Value 3"});
			this.ValueDoesSaveCombobox.Location = new System.Drawing.Point(220, 19);
			this.ValueDoesSaveCombobox.Name = "ValueDoesSaveCombobox";
			this.ValueDoesSaveCombobox.Size = new System.Drawing.Size(121, 23);
			this.ValueDoesSaveCombobox.Sorted = true;
			this.ValueDoesSaveCombobox.TabIndex = 1;
			this.ValueDoesSaveCombobox.SelectionChangeCommitted += new System.EventHandler(this.ValueDoesSaveCombobox_SelectionChangeCommitted);
			// 
			// InputForm
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 15F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(800, 450);
			this.Controls.Add(this.panel2);
			this.Controls.Add(this.panel1);
			this.Name = "InputForm";
			this.Text = "Input form";
			this.panel1.ResumeLayout(false);
			this.panel2.ResumeLayout(false);
			this.panel2.PerformLayout();
			this.groupBox1.ResumeLayout(false);
			this.groupBox1.PerformLayout();
			this.ResumeLayout(false);

	}

	#endregion

	private Panel panel1;
	private Button SaveAndCloseButton;
	private Button SaveButton;
	private Panel panel2;
	private Label label2;
	private ComboBox ValueDoesNotSaveCombobox;
	private Label label1;
	private ComboBox ValueDoesSaveCombobox;
	private GroupBox groupBox1;
	private RadioButton CannotSelectOption;
	private RadioButton AlwaysCheckedOption;
	private Label label3;
	private ComboBox ChosenValueResetToZeroCombobox;
}