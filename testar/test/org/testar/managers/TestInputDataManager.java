package org.testar.managers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestInputDataManager {

	@Rule
	public RepeatRule repeatRule = new RepeatRule();

	@Test
	@Repeat( times = 100 )
	public void obtainNumberInput() {
		String number = InputDataManager.getRandomNumberInput();
		System.out.println("test obtainNumberInput(): " + number);
		assertNotNull(number);
		assertTrue(!number.isEmpty());
		assertTrue(isNumeric(number));
	}

	@Test
	@Repeat( times = 100 )
	public void obtainAlphabeticInput() {
		String alphabetic = InputDataManager.getRandomAlphabeticInput(10);
		System.out.println("test obtainAlphabeticInput(): " + alphabetic);
		assertNotNull(alphabetic);
		assertTrue(alphabetic.length() == 10);
		assertTrue(alphabetic.matches("[a-zA-Z]+"));
	}

	//TODO: Do we want to create erroneous URLs intentionally? improve URL verification
	@Test
	@Repeat( times = 100 )
	public void obtainUrlInput() {
		String url = InputDataManager.getRandomUrlInput();
		System.out.println("test obtainUrlInput(): " + url);
		assertNotNull(url);
		assertTrue(!url.isEmpty());
	}

	//TODO: Do we want to create erroneous dates intentionally? improve date verification
	@Test
	@Repeat( times = 100 )
	public void obtainDateInput() {
		String date = InputDataManager.getRandomDateInput();
		System.out.println("test obtainDateInput(): " + date);
		assertNotNull(date);
		assertTrue(!date.isEmpty());
	}

	//TODO: Do we want to create erroneous email intentionally? improve email verification
	@Test
	@Repeat( times = 100 )
	public void obtainEmailInput() {
		String email = InputDataManager.getRandomEmailInput();
		System.out.println("test obtainEmailInput(): " + email);
		assertNotNull(email);
		assertTrue(!email.isEmpty());
	}

	@Test
	@Repeat( times = 100 )
	public void obtainTextInputData() {
		String text = InputDataManager.getRandomTextInputData();
		System.out.println("test obtainTextInputData(): " + text);
		assertNotNull(text);
		assertTrue(!text.isEmpty());
	}

	@Test
	@Repeat( times = 100 )
	public void obtainFileInputData() {
		// Redirect the error stream to assert that the file is being used by checking no error message is printed
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
		String fileData = InputDataManager.getRandomTextFromCustomInputDataFile(System.getProperty("user.dir") + "/resources/settings/custom_input_data.txt");
		System.out.println("test obtainFileInputData(): " + fileData);
		assertNotNull(fileData);
		assertTrue(!fileData.isEmpty());
		assertTrue(outContent.size() == 0);
	}

	private boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}

// https://gist.github.com/fappel/8bcb2aea4b39ff9cfb6e
// JUnit 4 TestRule to run a test repeatedly for a specified amount of repetitions
// TODO: When migrate to JUnit 5 use the @RepeatedTest annotation

@Retention( java.lang.annotation.RetentionPolicy.RUNTIME )
@Target( { java.lang.annotation.ElementType.METHOD } )
@interface Repeat {
	public abstract int times();
}

class RepeatRule implements TestRule {

	private static class RepeatStatement extends Statement {

		private final int times;
		private final Statement statement;

		private RepeatStatement( int times, Statement statement ) {
			this.times = times;
			this.statement = statement;
		}

		@Override
		public void evaluate() throws Throwable {
			for( int i = 0; i < times; i++ ) {
				statement.evaluate();
			}
		}
	}

	@Override
	public Statement apply( Statement statement, Description description ) {
		Statement result = statement;
		Repeat repeat = description.getAnnotation( Repeat.class );
		if( repeat != null ) {
			int times = repeat.times();
			result = new RepeatStatement( times, statement );
		}
		return result;
	}
}
