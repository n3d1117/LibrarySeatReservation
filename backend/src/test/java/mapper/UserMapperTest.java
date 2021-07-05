package mapper;

import dto.UserDto;
import model.ModelFactory;
import model.Role;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class UserMapperTest {

    private UserMapper mapper;
    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        mapper = new UserMapper();

        user = spy(ModelFactory.initializeUser());
        user.setEmail("some@email.com");
        user.setPassword("password");
        user.setRoles(Collections.singletonList(Role.BASIC));

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setRoles(Collections.singletonList(Role.BASIC.toString()));

        when(user.getId()).thenReturn(1L);
    }

    @Test
    public void testGenerateRedactedUserDTO() {
        UserDto generated = mapper.generateRedactedUserDTO(user);
        assertEquals(userDto.getId(), generated.getId());
        assertEquals(userDto.getEmail(), generated.getEmail());
        assertNull(generated.getPassword());
        assertIterableEquals(userDto.getRoles(), generated.getRoles());
    }

    @Test
    public void testGenerateWrongUserDTO() {
        user.setEmail("another@email.com");
        user.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));

        UserDto generated = mapper.generateRedactedUserDTO(user);
        assertNotEquals(userDto.getEmail(), generated.getEmail());
        assertNotEquals(userDto.getRoles(), generated.getRoles());
    }

    @Test
    public void testGenerateUserFromDTO() {
        User generated = mapper.generateUserFromDTO(userDto);
        assertEquals(user.getEmail(), generated.getEmail());
        assertEquals(user.getPassword(), generated.getPassword());
        assertIterableEquals(user.getRoles(), generated.getRoles());
    }

    @Test
    public void testGenerateWrongUserFromDTO() {
        userDto.setEmail("another@email.com");
        userDto.setPassword("another password");
        userDto.setRoles(Arrays.asList(Role.BASIC.toString(), Role.ADMIN.toString()));

        User generated = mapper.generateUserFromDTO(userDto);
        assertNotEquals(user.getEmail(), generated.getEmail());
        assertNotEquals(user.getPassword(), generated.getPassword());
        assertNotEquals(user.getRoles(), generated.getRoles());
    }

}
