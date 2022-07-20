package tourGuide.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDTO {

    /**
     * The user's name.
     */
    private String userName;

    /**
     * The user's phone number.
     */
    private String phoneNumber;

    /**
     * The user's email address.
     */
    private String emailAddress;

}
