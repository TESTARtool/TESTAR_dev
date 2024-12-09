package org.testar.otp.email;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.mail.FetchProfile;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder.FetchProfileItem;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;

public class ImapGmailReader {

	private final String email;
	private final char[] app_password;

	public ImapGmailReader(String email, char[] app_password) {
		this.email = email;
		this.app_password = app_password;
	}

	public String readOtpNumber(int seconds, String otpRegex) {
		String otpCode = "";

		Store store = null;
		Folder folder = null;

		try {
			store = getImapStore();
			folder = getFolderFromStore(store, "INBOX");

			// Search for yesterday messages
			Message[] messages = folder.search(getYesterdayMessages());
			folder.fetch(messages, getFetchProfile());

			// Compile the regex pattern
			Pattern pattern = Pattern.compile(otpRegex);

			for (Message message : messages) {
				// Extract email content as a string using the last 'seconds' interval
				String messageText = getMessageText(message, seconds);

				// Match the content against the OTP regex
				Matcher matcher = pattern.matcher(messageText);

				if (matcher.find()) {
					otpCode = matcher.group(0); // Capture group if present
					break; // Stop after finding the first OTP
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeFolder(folder);
			closeStore(store);
		}

		return otpCode;
	}

	Store getImapStore() throws Exception {
		Session session = Session.getInstance(getImapProperties());
		Store store = session.getStore("imaps");

		// Use new String(app_password) to convert char[] to String for authentication
		store.connect("imap.gmail.com", this.email, new String(this.app_password));

		// Clear the password from memory after use
		java.util.Arrays.fill(this.app_password, ' ');

		return store;
	}

	private Properties getImapProperties() {
		Properties props = new Properties();
		props.put("mail.imaps.host", "imap.gmail.com");
		props.put("mail.imaps.ssl.trust", "imap.gmail.com");
		props.put("mail.imaps.port", "993");
		props.put("mail.imaps.starttls.enable", "true");
		props.put("mail.imaps.connectiontimeout", "10000");
		props.put("mail.imaps.timeout", "10000");
		return props;
	}

	private Folder getFolderFromStore(Store store, String folderName) throws MessagingException {
		Folder folder = store.getFolder(folderName);
		folder.open(Folder.READ_ONLY);
		return folder;
	}

	private SearchTerm getYesterdayMessages() {
		Date yesterdayDate = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
		return new ReceivedDateTerm(ComparisonTerm.GT, yesterdayDate);
	}

	private FetchProfile getFetchProfile() {
		FetchProfile fetchProfile = new FetchProfile();
		fetchProfile.add(FetchProfileItem.ENVELOPE);
		fetchProfile.add(FetchProfileItem.CONTENT_INFO);
		fetchProfile.add("X-mailer");
		return fetchProfile;
	}

	private String getMessageText(Message message, int seconds) throws MessagingException, IOException {
		Date filteredDateBySeconds = new Date(System.currentTimeMillis() - (1000L * seconds));

		// If the message does not meet the time seconds criteria, return an empty string
		Date messageReceivedDate = message.getReceivedDate();
		if (messageReceivedDate == null || messageReceivedDate.before(filteredDateBySeconds)) {
			return "";
		}

		// Collect and return the text content of the message
		StringBuilder textCollector = new StringBuilder();
		collectTextFromMessage(textCollector, message);
		return textCollector.toString();
	}

	private void collectTextFromMessage(StringBuilder textCollector, Part part) throws MessagingException, IOException {
		if (part.isMimeType("text/plain")) {
			textCollector.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*") && part.getContent() instanceof Multipart) {
			Multipart multiPart = (Multipart) part.getContent();
			for (int i = 0; i < multiPart.getCount(); i++) {
				collectTextFromMessage(textCollector, multiPart.getBodyPart(i));
			}
		}
	}

	private void closeFolder(Folder folder) {
		if (folder != null && folder.isOpen()) {
			try {
				folder.close(true);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	private void closeStore(Store store) {
		if (store != null && store.isConnected()) {
			try {
				store.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
}
