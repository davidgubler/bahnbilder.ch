package entities.tmp;

import entities.VehicleType;
import entities.mongodb.MongoDbVehicleType;
import i18n.Txt;

import java.util.Objects;

public class OtherVehicleType implements VehicleType {
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName(String lang) {
        return Txt.get(lang, "other");
    }

    @Override
    public String getPlural(String lang) {
        return Txt.get(lang, "others");
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoDbVehicleType that = (MongoDbVehicleType) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
