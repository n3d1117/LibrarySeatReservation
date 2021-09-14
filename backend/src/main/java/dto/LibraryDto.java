package dto;

public class LibraryDto {

    private long id;
    private String name;
    private String imgFilename;
    private String address;
    private Integer capacity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImgFilename() { return imgFilename; }

    public void setImgFilename(String imgFilename) { this.imgFilename = imgFilename; }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
