import java.math.BigDecimal;

/**
 * A single job in an order: description, parts cost, labor cost and completion state.
 * Costs are final on purpose: the task forbids generic setPrice methods.
 */
public class ServiceJob {

    private String description;
    private final BigDecimal partsCost;
    private final BigDecimal laborCost;
    private boolean done;

    public ServiceJob(String description, BigDecimal partsCost, BigDecimal laborCost) {
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

    /**
     * Marks the job as done. Called only from ServiceOrder,
     * because the order checks whether this is allowed right now.
     */
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
