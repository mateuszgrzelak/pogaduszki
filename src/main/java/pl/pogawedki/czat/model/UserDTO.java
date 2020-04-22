package pl.pogawedki.czat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @Size(min=4, max=10, message = "{pl.pogawedki.validation.username.Size.message}")
    @Pattern(regexp = "[[A-Z][a-z][0-9]]+", message = "{pl.pogawedki.validation.username.Pattern.message}")
    private String username;
    @NotBlank(message = "{pl.pogawedki.validation.password.NotBlank.message}")
    private String password;
}
