package model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "libraries")
public class Library extends BaseEntity {

    private String name;
    private String address;
    private String imgFilename;
    private Integer capacity;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL)
    protected Set<Reservation> reservations = new HashSet<>();

    public Library() { }

    public Library(String uuid) {
        super(uuid);
    }

    @Override
    public void copy(BaseEntity a) {
        Library that = (Library) a;
        this.name = that.getName();
        this.imgFilename = that.getImgFilename();
        this.address = that.getAddress();
        this.capacity = that.getCapacity();
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

    public String getImgFilename() { return imgFilename; }

    public void setImgFilename(String imgFilename) { this.imgFilename = imgFilename; }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Library{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", filename='" + imgFilename +'\'' +
                ", capacity=" + capacity +
                ", reservations=" + reservations +
                '}';
    }
}
