/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2016-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.Lists;

/**
 * A management utility for SUT GUI input values data.
 */
public class InputDataManager {

	public static String getRandomTextFromCustomInputDataFile(String customDataFile) {
		try {
			List<String> allLines = Files.readAllLines(Paths.get(customDataFile));
			if (allLines != null && !allLines.isEmpty()) {
				return allLines.get(new Random().nextInt(allLines.size()));
			}
		} catch (IOException e) {
			System.err.println(Paths.get(customDataFile) + " is empty or file was not found. Returning a random text input data.");
		}

		return getRandomTextInputData();
	}

	/**
	 * Computes a random text data input from the available data types. 
	 * Number, Alphabetic, URL, Date or Email. 
	 * @return The random data input.
	 */
	public static String getRandomTextInputData(){
		switch(new Random().nextInt(5)){
		case 0:
			return getRandomNumberInput();
		case 1:
			return getRandomAlphabeticInput(10);
		case 2:
			return getRandomUrlInput();
		case 3:
			return getRandomDateInput();
		default:
			return getRandomEmailInput();
		}
	}

	/**
	 * Computes a random number
	 * @return The random number.
	 */
	public static String getRandomNumberInput(){
		Random rnd = new Random(System.currentTimeMillis());
		return Integer.toString(rnd.nextInt());
	}

	/**
	 * Computes a random alphabetic. 
	 * @return The random alphabetic.
	 */
	public static String getRandomAlphabeticInput(int count){
		if(count < 1) {
			System.err.println("Random Alphabetic Input length " + count + " cannot be less than 1. Return a random Alphabetic Input of length 10.");
			count = 10;
		}
		return RandomStringUtils.randomAlphabetic(count);
	}

	/**
	 * Computes a random URL.
	 * @return The random URL.
	 */
	public static String getRandomUrlInput(){
		List<String> urls = Lists.newArrayList(
				"www.foo.com", 
				"www.boo.com", 
				"www.fooboo.com",
				"www.foo.org",
				"www.boo.org",
				"www.fooboo.org",
				"www.testar.org");

		return urls.get(new Random().nextInt(urls.size()));
	}

	/**
	 * Computes a random date.
	 * @return The random date.
	 */
	public static String getRandomDateInput(){
		List<String> dates = Lists.newArrayList(
				"22-03-2017",
				"03-22-2017",
				"2017-03-22",
				"2017-22-03",
				"00-00-1900",
				"12-12-7357",
				"01-01-01",
				"2017",
				"November",
				"31 December",
				"1st January",
				"2001 February");

		return dates.get(new Random().nextInt(dates.size()));
	}

	/**
	 * Computes a random email.
	 * @return The random email.
	 */
	public static String getRandomEmailInput(){
		List<String> emails = Lists.newArrayList(
				"foo@boo.org",
				"boo@foo.org",
				"fooboo@org.com",
				"foo-boo@foo.com");

		return emails.get(new Random().nextInt(emails.size()));
	}
}
