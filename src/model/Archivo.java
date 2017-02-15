package model;

public class Archivo {

    private String name;
    private String location;

    public Archivo(String name, String location) {
        this.name = name;
        this.location = location + ".rj";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location + ".rj";
    }

    @Override
    public String toString() {
        return name;
    }
}
