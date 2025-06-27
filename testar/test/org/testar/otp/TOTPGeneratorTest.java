package org.testar.otp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class TOTPGeneratorTest {

	@Test
	public void testGenerateTOTP() throws Exception {
		char[] secretKey = "SECRETOTPKEY".toCharArray();
		TOTPGenerator totpGenerator = new TOTPGenerator(secretKey);

		String otp = totpGenerator.generateTOTP();

		assertNotNull(otp);
		assertTrue(!otp.isEmpty());
		assertEquals(6, otp.length());
		assertTrue(otp.matches("\\d+"));
	}

}

