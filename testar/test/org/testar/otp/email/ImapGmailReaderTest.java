package org.testar.otp.email;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Store;
import jakarta.mail.search.SearchTerm;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ImapGmailReaderTest {

	@Mock
	private Store store;

	@Mock
	private Folder folder;

	@Mock
	private Message message;

	@InjectMocks
	private ImapGmailReader imapGmailReader;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		imapGmailReader = spy(new ImapGmailReader("test@example.com", "app_password".toCharArray()));

		doReturn(store).when(imapGmailReader).getImapStore();

		when(store.getFolder("INBOX")).thenReturn(folder);
		when(folder.search(any(SearchTerm.class))).thenReturn(new Message[]{message});
		when(folder.isOpen()).thenReturn(true);
		when(message.getReceivedDate()).thenReturn(new Date());
		when(message.isMimeType("text/plain")).thenReturn(true);
	}

	@Test
	public void testMockOtpEmailNumber() throws Exception {
		when(message.getContent()).thenReturn("Dear client, Your OTP is 123456. Regards, TESTAR team.");

		String otp = imapGmailReader.readOtpNumber(60, "\\d{6}");

		// Assert: verify the expected OTP
		assertNotNull(otp);
		assertEquals("123456", otp);
	}

	@Test
	public void testMockOtpEmptyNumber() throws Exception {
		when(message.getContent()).thenReturn("Dear client, there are no OTP numbers in this email. Regards, TESTAR team.");

		String otp = imapGmailReader.readOtpNumber(60, "\\d{6}");

		// Assert: verify the empty OTP
		assertNotNull(otp);
		assertTrue(otp.isEmpty());
	}

	@Ignore
	@Test
	public void testRealOtpEmail() throws Exception {
		ImapGmailReader realImapGmailReader = new ImapGmailReader("email@gmail.com", "xxxx yyyy zzzz tttt".toCharArray());

		String otp = realImapGmailReader.readOtpNumber(60, "\\d{6}");

		System.out.println("testRealOtpEmail OTP: " + otp);

		// Assert: verify the real OTP number
		assertNotNull(otp);
		assertTrue(!otp.isEmpty());
		assertEquals("XYZOTP", otp);
	}
}
