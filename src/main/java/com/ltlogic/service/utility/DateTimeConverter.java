/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.utility;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author Bishistha
 */
@Converter(autoApply = true)
public class DateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
     return localDateTime != null ? Timestamp.valueOf(localDateTime) : null;
   }

  @Override
  public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
     return timestamp != null ? timestamp.toLocalDateTime() : null;
   }
}
