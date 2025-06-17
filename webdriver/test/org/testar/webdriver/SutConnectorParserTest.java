package org.testar.webdriver;

import java.util.Objects;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.testar.monkey.Assert;

public class SutConnectorParserTest {

	private static final String BROWSER_PATH = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	private static final String URL = "https://para.testar.org";
	private static final String SCREEN = "1280x800+0+0";

	@Test
	public void testCorrectParsingUrl() {
		String input = " \"" + URL + "\" ";
		SutConnectorParser parser = new SutConnectorParser(input);

		Assert.isTrue(parser.getBrowserPath().isEmpty());
		Assert.isEquals(URL, parser.getUrl());
		Assert.isTrue(Objects.isNull(parser.getScreenDimensions()));
		Assert.isTrue(Objects.isNull(parser.getScreenPosition()));
	}

	@Test
	public void testCorrectParsingPathUrl() {
		String input = " \"" + BROWSER_PATH + "\" \"" + URL + "\" ";
		SutConnectorParser parser = new SutConnectorParser(input);

		Assert.isEquals(BROWSER_PATH, parser.getBrowserPath());
		Assert.isEquals(URL, parser.getUrl());
		Assert.isTrue(Objects.isNull(parser.getScreenDimensions()));
		Assert.isTrue(Objects.isNull(parser.getScreenPosition()));
	}

	@Test
	public void testCorrectParsingInOrder() {
		String input = "\"" + BROWSER_PATH + "\" \"" + URL + "\" \"" + SCREEN + "\"";
		SutConnectorParser parser = new SutConnectorParser(input);

		Assert.isEquals(BROWSER_PATH, parser.getBrowserPath());
		Assert.isEquals(URL, parser.getUrl());
		Assert.isEquals(new Dimension(1280, 800), parser.getScreenDimensions());
		Assert.isEquals(new Point(0, 0), parser.getScreenPosition());
	}

	@Test
	public void testCorrectParsingMixedOrder() {
		String input = "\"" + SCREEN + "\" \"" + URL + "\" \"" + BROWSER_PATH + "\"";
		SutConnectorParser parser = new SutConnectorParser(input);

		Assert.isEquals(BROWSER_PATH, parser.getBrowserPath());
		Assert.isEquals(URL, parser.getUrl());
		Assert.isEquals(new Dimension(1280, 800), parser.getScreenDimensions());
		Assert.isEquals(new Point(0, 0), parser.getScreenPosition());
	}

}
