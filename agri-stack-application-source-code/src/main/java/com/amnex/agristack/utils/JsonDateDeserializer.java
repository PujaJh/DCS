package com.amnex.agristack.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateDeserializer implements JsonDeserializer<Date> {

    public Date deserialize(JsonElement json,
                            Type date,
                            JsonDeserializationContext context)
            throws JsonParseException {
        String stringDate = json.getAsJsonPrimitive().getAsString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(stringDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}