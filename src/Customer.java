import java.util.Objects;


public class Customer {

    private final long id;
    private String fullName;
    private String phone;

    public Customer(long id, String fullName, String phone) {
        this.id = id;
        setFullName(fullName);
        setPhone(phone);
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = Validation.requireText(fullName, "Customer name");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = Validation.requireText(phone, "Customer phone");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return fullName + " (" + phone + ")";
    }
}
