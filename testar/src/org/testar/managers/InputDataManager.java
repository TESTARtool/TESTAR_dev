/***************************************************************************************************
 *
 * Copyright (c) 2016 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

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
	 * Use a widget information to compute a random text data input.
	 * @param w
	 * @return
	 */
	public static String getRandomTextInputData(Widget w){
		// Use some widget Tag property to determine the type of input data
		if(w.get(WdTags.WebType, "").toLowerCase().contains("email")) { return getRandomEmailInput(); }
		if(w.get(WdTags.WebType, "").toLowerCase().contains("number")) { return getRandomNumberInput(); }
		if(w.get(WdTags.WebType, "").toLowerCase().contains("url")) { return getRandomUrlInput(); }
		if(w.get(WdTags.WebType, "").equalsIgnoreCase("date")) { return getRandomDateNumber(); }
		if(w.get(WdTags.WebType, "").equalsIgnoreCase("time")) { return getRandomTimeNumber(); }
		if(w.get(WdTags.WebType, "").equalsIgnoreCase("week")) { return getRandomWeekNumber(); }

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
				"https://www.foo.com",
				"http://www.boo.com",
				"https://www.fooboo.com",
				"http://www.foo.org",
				"https://www.boo.org",
				"http://www.fooboo.org",
				"https://www.testar.org");

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

	public static String getRandomDateNumber()
	{
		List<String> dates = Lists.newArrayList(
				"22032017",
				"03222017",
				"22032017",
				"01011900",
				"12127357",
				"01011001");

		return dates.get(new Random().nextInt(dates.size()));
	}

	public static String getRandomTimeNumber()
	{
		List<String> times = Lists.newArrayList(
				"2217",
				"2220",
				"0355",
				"0132",
				"1212",
				"1101");

		return times.get(new Random().nextInt(times.size()));
	}

	public static String getRandomWeekNumber()
	{
		String week = Integer.toString(new Random().nextInt(51)+1);
		String year = Integer.toString(new Random().nextInt(9999));
		return week + year;
	}
}
