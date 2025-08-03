package entities.hardcoded;

import entities.License;

import java.util.HashMap;
import java.util.Map;

public class HardcodedLicense implements License {
    private final int id;

    private final String name;

    private final Map<String, String> logo = new HashMap<>();

    public HardcodedLicense(int id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo.put("de", logo);
        this.logo.put("en", logo);
    }

    public HardcodedLicense(int id, String name, String logoDe, String logoEn) {
        this.id = id;
        this.name = name;
        this.logo.put("de", logoDe);
        this.logo.put("en", logoEn);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLogo(String lang) {
        return logo.get(lang);
    }

    @Override
    public String toString() {
        return getName();
    }
}
