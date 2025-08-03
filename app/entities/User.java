package entities;

import java.util.List;

public interface User extends LocalizedEntity {
    int getId();

    String getName();

    default String getName(String lang) {
        return getName();
    }

    String getEmail();

    List<? extends Session> getSessions();

    boolean checkPassword(String password);

    int getDefaultLicenseId();

    boolean isAdmin();

    default boolean canEdit(Travelogue t) {
        return t != null && (isAdmin() || this.getId() == t.getUserId());
    }

    default boolean canEdit(Photo photo) {
        return this.isAdmin() || this.equals(photo.getUser());
    }
}
