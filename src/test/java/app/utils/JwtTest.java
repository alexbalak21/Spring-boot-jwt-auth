package app.utils;

import app.dto.UserDetailsDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class JwtTest {

    @Test
    public void testJwtGenerationAndValidation() {
        // Step 1: Create UserDetailsDTO object with test data
        UserDetailsDTO originalUser = new UserDetailsDTO("alex@email.com", "cadfcee2-43ab-46c2-a111-921fed5a0056", "VISITOR");

        // Step 2: Generate JWT token
        String token = Jwt.generateToken(originalUser);
        System.out.println("Generated Token: " + token);

        // Step 3: Validate token
        boolean isValid = Jwt.validateToken(token);
        assertThat(isValid).isTrue();

        // Step 4: Decode token to get UserDetailsDTO
        UserDetailsDTO decodedUser = Jwt.getUserDetailsFromToken(token);
        System.out.println("Decoded UserDetailsDTO: " + decodedUser);

        // Step 5: Verify original data matches decoded data
        assertThat(decodedUser.getEmail()).isEqualTo(originalUser.getEmail());
        assertThat(decodedUser.getUid()).isEqualTo(originalUser.getUid());
        assertThat(decodedUser.getRole()).isEqualTo(originalUser.getRole());
    }
}
