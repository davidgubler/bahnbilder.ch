package entities.tmp;

import entities.Location;

public class TmpLocation implements Location {
    private final String name;

    public TmpLocation(String name) {
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
}
