package my.edu.utar.inventorycontrol;

public class UserClass {
    public static String email, name, password, phoneNo, username;

    public UserClass(String email, String name, String password, String phoneNo, String username) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNo = phoneNo;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
