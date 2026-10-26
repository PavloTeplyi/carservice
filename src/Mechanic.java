import java.util.*;


public class Mechanic {

    private final Long id;
    private String fullName;
    private Set<Specialization> specializations = new HashSet<>();
    private boolean busy;

    public Mechanic(Long id, String fullName, Specialization... specializations) {
        this.id = id;
        setFullName(fullName);
        setSpecializations(specializations);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = Validation.requireText(fullName, "Mechanic name");
    }

    public Set<Specialization> getSpecializations() {
        return Collections.unmodifiableSet(specializations);
    }

    public void setSpecializations(Specialization... newSpecializations) {
        if (newSpecializations == null || newSpecializations.length == 0) {
            throw new IllegalArgumentException("Mechanic must have at least one specialization");
        }
        Set<Specialization> result = new HashSet<>();
        for (Specialization s : newSpecializations) {
            result.add(Validation.requireNonNull(s, "Specialization"));
        }
        specializations = result;
    }

    public boolean canDo(Job job) {
        return specializations.contains(job.getSpecialization());
    }

    public boolean isBusy() {
        return busy;
    }

    void occupy() {
        if (busy) {
            throw new IllegalStateException("Mechanic " + fullName + " is already busy with another order");
        }
        busy = true;
    }


    void release() {
        busy = false;
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
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return fullName + " " + specializations;
    }
}
