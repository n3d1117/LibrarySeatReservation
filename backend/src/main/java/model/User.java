package model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true)
    private String username;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    public User() { }

    public User(String uuid) {
        super(uuid);
    }

    @Override
    public void copy(BaseEntity a) {
        User that = (User) a;
        this.username = that.getUsername();
        this.password = that.getPassword();
        this.roles = that.getRoles();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
