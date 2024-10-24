package com.amnex.agristack.notifications;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.amnex.agristack.dao.common.Mail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


public class NotificationThread extends Thread {

	Mail mail;

	JavaMailSender mailSender;

	@Override
	public void run() {
		sendEmail(mail, mailSender);
	}

	public NotificationThread(Mail mail, JavaMailSender mailSender) {
		this.mail = mail;
		this.mailSender = mailSender;
	}

	public void sendEmail(Mail mail, JavaMailSender mailSender) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

			mimeMessageHelper.setSubject(mail.getMailSubject());
			mimeMessageHelper.setFrom(new InternetAddress(mail.getMailFrom(), "amnex.com"));
			mimeMessageHelper.setTo(mail.getMailTo());
			mimeMessageHelper.setCc(mail.getMailCc());
			mimeMessageHelper.setText(mail.getMailContent(), true);
			mailSender.send(mimeMessageHelper.getMimeMessage());

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
