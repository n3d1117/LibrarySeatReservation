package controller;

import com.google.gson.Gson;
import dao.UserDao;
import dto.UserDto;
import mapper.UserMapper;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class UserController {

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserDao userDao;

    public String all() {
        List<UserDto> userDTOList = new ArrayList<>();
        for (User user: userDao.all())
            userDTOList.add(userMapper.generateRedactedUserDTO(user));
        return new Gson().toJson(userDTOList);
    }

    public String find(Long id) {
        User user = userDao.findById(id);
        return new Gson().toJson(userMapper.generateRedactedUserDTO(user));
    }

    public String add(String json) {
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(json, UserDto.class);
        User newUser = userMapper.generateUserFromDTO(userDto);
        userDao.save(newUser);
        if (newUser.getId() != null)
            userDto.setId(newUser.getId());
        return gson.toJson(userDto);
    }

    public void update(String body) {
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(body, UserDto.class);
        User updatedUser = userMapper.generateUserFromDTO(userDto);
        userDao.update(updatedUser, userDto.getId());
    }

    public void delete(Long id) {
        userDao.delete(id);
    }

    public String login(String email, String password) {
        return new Gson().toJson(userMapper.generateRedactedUserDTO(userDao.login(email, password)));
    }
}
