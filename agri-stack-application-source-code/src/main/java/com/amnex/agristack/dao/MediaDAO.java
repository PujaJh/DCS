package com.amnex.agristack.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MediaDAO {

	public String media_id;
	public String media_type;
	public String media_url;
	public String activity_code;

	public MediaDAO(String media_id, String media_url) {
		this.media_id = media_id;
		this.media_url = media_url;
	}
}
