using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace BuggyProgram;
public partial class SettingsForm : Form
{
	public static int SavedComboboxValue { get; set; } = 0;

	public SettingsForm()
	{
		InitializeComponent();
		SaveAndCloseButton.Enabled = false;
		SaveButton.Enabled = false;
		ValueDoesSaveCombobox.SelectedIndex = SavedComboboxValue;
		ValueDoesNotSaveCombobox.SelectedIndex = 0;
		ChosenValueResetToZeroCombobox.SelectedIndex = 0;
	}

	private void enableSaveButtons()
	{
		SaveButton.Enabled = true;
		SaveAndCloseButton.Enabled = true;
	}
	private void SaveButton_Click(object sender, EventArgs e)
	{
		ValueDoesNotSaveCombobox.SelectedIndex = 0;
		SavedComboboxValue = ValueDoesSaveCombobox.SelectedIndex;
	}

	private void SaveAndCloseButton_Click(object sender, EventArgs e)
	{
		SaveButton_Click(sender, e);
		Close();
	}

	private void CannotSelectOption_CheckedChanged(object sender, EventArgs e)
	{
		Thread.Sleep(400);
		AlwaysCheckedOption.Checked = true;
		enableSaveButtons();
	}

	private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
	{
		ChosenValueResetToZeroCombobox.SelectedIndex = 0;
	}

	private void panel2_Paint(object sender, PaintEventArgs e)
	{

	}

	private void ValueDoesSaveCombobox_SelectionChangeCommitted(object sender, EventArgs e)
	{
		enableSaveButtons();
	}

	private void ValueDoesNotSaveCombobox_SelectionChangeCommitted(object sender, EventArgs e)
	{
		enableSaveButtons();
	}
}
