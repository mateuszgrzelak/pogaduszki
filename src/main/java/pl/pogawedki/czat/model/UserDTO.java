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
    @Pattern(regexp = "[\\w]+", message = "{pl.pogawedki.validation.username.Pattern.message}")
    private String username;
    @NotBlank(message = "{pl.pogawedki.validation.password.NotBlank.message}")
    private String password;
    @NotBlank(message = "{pl.pogawedki.validation.description.NotBlank.message}")
    @Size(max=200, message = "{pl.pogawedki.validation.description.Size.message}")
    private String description;
}
