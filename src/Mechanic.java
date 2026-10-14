import java.util.Objects;


public class Mechanic {

    private final long id;
    private String fullName;
    private String specialization;
    private Order currentOrder;

    public Mechanic(long id, String fullName, String specialization) {
        this.id = id;
        setFullName(fullName);
        setSpecialization(specialization);
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = Validation.requireText(fullName, "Mechanic name");
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = Validation.requireText(specialization, "Specialization");
    }

    public boolean isBusy() {
        return currentOrder != null;
    }

    /**
     * Assigns the mechanic to an order. Called only from Order.
     */
    void occupy(Order order) {
        if (isBusy()) {
            throw new IllegalStateException("Mechanic " + fullName
                    + " is already working on order #" + currentOrder.getId());
        }
        currentOrder = order;
    }

    /**
     * Frees the mechanic after the order is completed or cancelled.
     */
    void release() {
        currentOrder = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mechanic)) {
            return false;
        }
        Mechanic other = (Mechanic) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return fullName + " (" + specialization + ")";
    }
}
