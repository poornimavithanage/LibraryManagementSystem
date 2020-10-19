package util;

public class MemberTM {
    private String nic;
    private String name;
    private String address;

    public MemberTM() {
    }

    public MemberTM(String nic, String name, String address) {
        this.nic = nic;
        this.name = name;
        this.address = address;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
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

    @Override
    public String toString() {
        return nic;
    }
}
