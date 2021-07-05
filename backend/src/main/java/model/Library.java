package model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "libraries")
public class Library extends BaseEntity {

    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    public Library() { }

    public Library(String uuid) {
        super(uuid);
    }

    @Override
    public void copy(BaseEntity a) {
        Library that = (Library) a;
        this.name = that.getName();
        this.address = that.getAddress();
        this.latitude = that.getLatitude();
        this.longitude = that.getLongitude();
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
