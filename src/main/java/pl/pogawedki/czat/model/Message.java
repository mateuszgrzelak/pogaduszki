package pl.pogawedki.czat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
  private String from;
  private String to;
  private String content;
  private String date;

}
