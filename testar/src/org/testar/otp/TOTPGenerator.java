/**
 *
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
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
 *
 */

package org.testar.otp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

public class TOTPGenerator {

	private final char[] secretKey;

	// Time step in seconds (30 seconds is standard for TOTP)
	private final int TIME_STEP = 30;

	/**
	 * Constructs a TOTPGenerator instance using a secret key
	 */
	public TOTPGenerator(char[] secretKey) {
		this.secretKey = secretKey;
	}

	public String generateTOTP() {
		try {
			long currentTimeSeconds = System.currentTimeMillis() / 1000;
			long timeStep = currentTimeSeconds / TIME_STEP;

			// Convert time step into a byte array (8 bytes)
			byte[] timeBytes = new byte[8];
			for (int i = 7; i >= 0; i--) {
				timeBytes[i] = (byte) (timeStep & 0xFF);
				timeStep >>= 8;
			}

			// Convert char[] secret key to byte[] securely
			byte[] keyBytes = decodeBase32(new String(secretKey));

			// Create HMAC-SHA1 hash
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA1");
			mac.init(keySpec);
			byte[] hash = mac.doFinal(timeBytes);

			// Extract offset from last nibble
			int offset = hash[hash.length - 1] & 0xF;

			// Generate 4-byte dynamic binary code
			int binaryCode = ((hash[offset] & 0x7F) << 24)
					| ((hash[offset + 1] & 0xFF) << 16)
					| ((hash[offset + 2] & 0xFF) << 8)
					| (hash[offset + 3] & 0xFF);

			// Convert to 6-digit OTP
			int otp = binaryCode % 1_000_000;

			// Pad with leading zeros if necessary
			return String.format("%06d", otp);
		} catch (Exception e) {
			System.err.println("Exception trying to generateTOTP");
			return "";
		}
	}

	// Decode a Base32-encoded string into a byte array
	private byte[] decodeBase32(String base32) {
		Base32 base32Decoder = new Base32();
		return base32Decoder.decode(base32);
	}

	public void clearSecretKey() {
		for (int i = 0; i < secretKey.length; i++) {
			secretKey[i] = '\0';
		}
	}

}
