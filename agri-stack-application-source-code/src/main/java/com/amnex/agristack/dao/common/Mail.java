package com.amnex.agristack.dao.common;

import java.io.StringWriter;
import java.util.List;

import lombok.Data;

@Data
public class Mail {

	private String mailFrom;

	private String[] mailTo;

	private String[] mailCc;

	private String mailBcc;

	private String mailSubject;

	private String mailContent;

	private StringWriter mailContentWriter;

	private String contentType;

	private List<Object> attachments;

	public Mail() {
		contentType = "text/plain";
	}
}
