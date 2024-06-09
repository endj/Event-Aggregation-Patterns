package com.example.e.messaging.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;

public class Json {
  private static final ObjectWriter WRITER;
  private static final ObjectReader READER;
  static {
    ObjectMapper mapper = new ObjectMapper()
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .registerModule(new JavaTimeModule());
    WRITER = mapper.writer();
    READER = mapper.reader();
  }


  public static String toJson(Object t) {
    try {
      return WRITER.writeValueAsString(t);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T fromJson(String jsonString, Class<T> type) {
    try {
      return READER.readValue(jsonString, type);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
