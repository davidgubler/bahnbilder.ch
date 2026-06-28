package entities.dummy;

import entities.Session;
import entities.User;

import java.util.List;

public class DummyUser implements User {
    private final String name;

    public DummyUser(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public List<? extends Session> getSessions() {
        return List.of();
    }

    @Override
    public boolean checkPassword(String password) {
        return false;
    }

    @Override
    public int getDefaultLicenseId() {
        return 0;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }
}
