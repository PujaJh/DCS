package com.amnex.agristack.centralcore.dao;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class HeaderDAO {
	public String version;
	public String message_id;
	public String message_ts;
    public String action = "on-seek";
    public String status;
    public String status_reason_code = "NA";
    public String status_reason_message = null;
	public int total_count;
    public int completed_count;
	public String sender_id;
	public String receiver_id;
	public Boolean is_msg_encrypted;
	public String sender_uri;
}
