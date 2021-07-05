package mapper;

import dto.UserDto;
import model.ModelFactory;
import model.Role;
import model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public UserDto generateRedactedUserDTO(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        serializeRoles(userDto, user.getRoles());
        return userDto;
    }

    public User generateUserFromDTO(UserDto dto) {
        User user = ModelFactory.initializeUser();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        deserializeRoles(dto.getRoles(), user);
        return user;
    }

    private void serializeRoles(UserDto dto, List<Role> roles) {
        if (roles != null && !roles.isEmpty()) {
            dto.setRoles(roles.stream().map(Role::toString).collect(Collectors.toList()));
        } else {
            dto.setRoles(Collections.singletonList(Role.BASIC.toString()));
        }
    }

    private void deserializeRoles(List<String> roles, User user) {
        if (roles != null && !roles.isEmpty()) {
            user.setRoles(roles.stream().map(Role::valueOf).collect(Collectors.toList()));
        } else {
            user.setRoles(Collections.singletonList(Role.BASIC));
        }
    }
}
