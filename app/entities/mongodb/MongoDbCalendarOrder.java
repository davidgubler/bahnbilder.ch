package entities.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import entities.CalendarOrder;
import org.bson.types.ObjectId;

import java.time.Instant;

@Entity(value = "calendarOrders", useDiscriminator = false)
public class MongoDbCalendarOrder implements MongoDbEntity, CalendarOrder {
    @Id
    private ObjectId _id;
    private Integer orderYear;
    private Instant timestamp;
    private int nrOfRailCalendars;
    private int nrOfAnimalCalendars;
    private String firstName;
    private String lastName;
    private String email;
    private String addressStreetAndNr;
    private String addressRemarks;
    private String zip;
    private String city;
    private String country;

    public MongoDbCalendarOrder()  {
        // dummy for Morphia
    }

    public MongoDbCalendarOrder(int orderYear, int nrOfRailCalendars, int nrOfAnimalCalendars, String email, String firstName, String lastName, String addressStreetAndNr, String addressRemarks, String zip, String city, String country) {
        this.orderYear = orderYear;
        this.timestamp = Instant.now();
        this.nrOfRailCalendars = nrOfRailCalendars;
        this.nrOfAnimalCalendars = nrOfAnimalCalendars;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addressStreetAndNr = addressStreetAndNr;
        this.addressRemarks = addressRemarks;
        this.zip = zip;
        this.city = city;
        this.country = country;
    }


    @Override
    public ObjectId getObjectId() {
        return _id;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public int getNrOfRailCalendars() {
        return nrOfRailCalendars;
    }

    @Override
    public int getNrOfAnimalCalendars() {
        return nrOfAnimalCalendars;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getAddressStreetAndNr() {
        return addressStreetAndNr;
    }

    @Override
    public String getAddressRemarks() {
        return addressRemarks;
    }

    @Override
    public String getZip() {
        return zip;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String getCountry() {
        return country;
    }
}
