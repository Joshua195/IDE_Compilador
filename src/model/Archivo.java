package model;

public class Archivo {

    private String name;
    private String location;
    private boolean status;
    private boolean saveStatus;

    public Archivo(String name, String location) {
        this.name = name;
        this.location = location;
        saveStatus = false;
        status = false;
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
        this.location = location;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(boolean saveStatus) {
        this.saveStatus = saveStatus;
    }

    @Override
    public String toString() {
        return name;
    }
}
