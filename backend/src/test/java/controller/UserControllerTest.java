package controller;

import com.google.gson.Gson;
import dao.UserDao;
import dto.UserDto;
import mapper.UserMapper;
import model.ModelFactory;
import model.Role;
import model.User;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController controller;

    private UserDao dao;
    private UserDto dto;
    private User user;

    @BeforeEach
    public void setup() throws IllegalAccessException {
        controller = new UserController();

        dao = mock(UserDao.class);

        user = spy(ModelFactory.initializeUser());
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));
        when(user.getId()).thenReturn(1L);

        dto = new UserDto();
        dto.setId(1L);
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setRoles(Arrays.asList(Role.BASIC.toString(), Role.ADMIN.toString()));

        FieldUtils.writeField(controller, "userMapper", new UserMapper(), true);
        FieldUtils.writeField(controller, "userDao", dao, true);
    }

    @Test
    public void testRetrieveAllUsers() {
        user.setPassword(null);
        dto.setPassword(null);

        User anotherUser = spy(ModelFactory.initializeUser());
        anotherUser.setUsername("another username");
        when(anotherUser.getId()).thenReturn(2L);

        UserDto anotherDto = new UserDto();
        anotherDto.setId(2L);
        anotherDto.setUsername(anotherUser.getUsername());
        anotherDto.setRoles(Collections.singletonList(Role.BASIC.toString()));

        when(dao.all()).thenReturn(Arrays.asList(user, anotherUser));
        assertEquals(controller.all(), new Gson().toJson(Arrays.asList(dto, anotherDto)));
    }

    @Test
    public void testFindById() {
        user.setPassword(null);
        dto.setPassword(null);
        when(dao.findById(1L)).thenReturn(user);
        assertEquals(controller.find(1L), new Gson().toJson(dto));
    }

    @Test
    public void testAddUser() {
        String dtoJson = new Gson().toJson(dto);

        assertEquals(controller.add(dtoJson), dtoJson);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(dao).save(argument.capture());
        assertEquals(user.getUsername(), argument.getValue().getUsername());
        assertEquals(user.getPassword(), argument.getValue().getPassword());
    }

    @Test
    public void testUpdateUser() {
        user.setPassword("another password");
        user.setRoles(Collections.singletonList(Role.BASIC));
        dto.setPassword(user.getPassword());
        dto.setRoles(Collections.singletonList(Role.BASIC.toString()));

        String dtoJson = new Gson().toJson(dto);
        controller.update(dtoJson);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(dao).update(argument.capture(), eq(dto.getId()));
        assertEquals(user.getPassword(), argument.getValue().getPassword());
        assertIterableEquals(user.getRoles(), argument.getValue().getRoles());
    }

    @Test
    public void testDeleteUser() {
        controller.delete(dto.getId());
        verify(dao).delete(dto.getId());
    }
}
