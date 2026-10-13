import java.time.Year;
import java.util.Objects;

/**
 * A car. It is uniquely identified by its VIN, so the VIN never changes.
 */
public class Car {

    private static final int FIRST_CAR_YEAR = 1886;

    private final String vin;
    private String make;
    private String model;
    private int year;
    private Customer owner;

    public Car(String vin, String make, String model, int year, Customer owner) {
        this.vin = Validation.requireText(vin, "VIN").toUpperCase();
        setMake(make);
        setModel(model);
        setYear(year);
        setOwner(owner);
    }

    public String getVin() {
        return vin;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = Validation.requireText(make, "Make");
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = Validation.requireText(model, "Model");
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < FIRST_CAR_YEAR || year > currentYear) {
            throw new IllegalArgumentException("Invalid production year: " + year);
        }
        this.year = year;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = Validation.requireNonNull(owner, "Owner");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }
        Car other = (Car) o;
        return vin.equals(other.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

    @Override
    public String toString() {
        return make + " " + model + " " + year + " [VIN " + vin + "]";
    }
}
