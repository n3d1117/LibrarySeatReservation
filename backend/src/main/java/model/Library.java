package model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "libraries")
public class Library extends BaseEntity {

    private String name;
    private String address;

    public Library() { }

    public Library(String uuid) {
        super(uuid);
    }

    @Override
    public void copy(BaseEntity a) {
        Library that = (Library) a;
        this.name = that.getName();
        this.address = that.getAddress();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
