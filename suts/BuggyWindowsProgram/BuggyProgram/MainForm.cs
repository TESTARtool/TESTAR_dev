namespace BuggyProgram;

public partial class MainForm : Form
{
	public MainForm()
	{
		InitializeComponent();
	}

	private void button1_Click(object sender, EventArgs e)
	{
		MessageBox.Show("Correct");
	}

	private void WrongAncorsBottomButton_Click(object sender, EventArgs e)
	{
		MessageBox.Show("WrongAncorsBottom");
	}

	private void WrongAncorsTopButton_Click(object sender, EventArgs e)
	{
		MessageBox.Show("WrongAncorsTop");
	}

	private void button2_Click(object sender, EventArgs e)
	{

	}

	private void ShowInputFormButton_Click(object sender, EventArgs e)
	{
		using var form = new SettingsForm();
		form.ShowDialog();
	}

	private void SearchFormButton_Click(object sender, EventArgs e)
	{
		using var form = new SearchForm();
		form.ShowDialog();
	}
}
