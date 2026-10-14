import java.math.BigDecimal;


public class Job {

    private String description;
    private final BigDecimal partsCost;
    private final BigDecimal laborCost;
    private boolean done;

    public Job(String description, BigDecimal partsCost, BigDecimal laborCost) {
        setDescription(description);
        this.partsCost = Validation.requireNonNegative(partsCost, "Parts cost");
        this.laborCost = Validation.requireNonNegative(laborCost, "Labor cost");
        this.done = false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Validation.requireText(description, "Job description");
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public BigDecimal getTotalCost() {
        return partsCost.add(laborCost);
    }

    public boolean isDone() {
        return done;
    }


    void markDone() {
        if (done) {
            throw new IllegalStateException("Job \"" + description + "\" is already done");
        }
        done = true;
    }

    @Override
    public String toString() {
        return description
                + " [parts " + partsCost
                + ", labor " + laborCost
                + ", " + (done ? "done" : "not done") + "]";
    }
}
