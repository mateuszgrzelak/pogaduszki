package pl.pogawedki.czat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassWithOnlyOneFieldString {

    @NotBlank
    private String value;
}
