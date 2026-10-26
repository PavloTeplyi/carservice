import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Order {

    private final Long id;
    private final Car car;
    private final List<Job> jobs = new ArrayList<>();
    private Mechanic mechanic;
    private OrderStatus status;


    Order(Long id, Car car) {
        this.id = id;
        this.car = Validation.requireNonNull(car, "Car");
        this.status = OrderStatus.CREATED;
    }

    public Long getId() {
        return id;
    }

    public Car getCar() {
        return car;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public OrderStatus getStatus() {
        return status;
    }


    public List<Job> getJobs() {
        return Collections.unmodifiableList(jobs);
    }

    public void addJob(Job job) {
        Validation.requireNonNull(job, "Job");
        ensureNotClosed();
        if (status != OrderStatus.CREATED && status != OrderStatus.DIAGNOSED) {
            throw new IllegalStateException("Jobs can be added only before approval, current status: " + status);
        }
        if (mechanic != null) {
            ensureCanDo(mechanic, job);
        }
        jobs.add(job);
    }

    public void diagnose() {
        changeStatus(OrderStatus.DIAGNOSED);
    }

    public void approve() {
        ensureNotClosed();
        if (jobs.isEmpty()) {
            throw new IllegalStateException("Cannot approve order #" + id + " without any jobs");
        }
        changeStatus(OrderStatus.APPROVED);
    }

    public void assignMechanic(Mechanic newMechanic) {
        Validation.requireNonNull(newMechanic, "Mechanic");
        ensureNotClosed();
        if (status == OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot change the mechanic while the order is in progress");
        }
        if (mechanic != null) {
            throw new IllegalStateException("Order #" + id + " already has mechanic " + mechanic.getFullName());
        }
        for (Job job : jobs) {
            ensureCanDo(newMechanic, job);
        }
        newMechanic.occupy();
        mechanic = newMechanic;
    }

    public void start() {
        ensureNotClosed();
        if (mechanic == null) {
            throw new IllegalStateException("Cannot start order #" + id + " without an assigned mechanic");
        }
        changeStatus(OrderStatus.IN_PROGRESS);
    }

    public void completeJob(int index) {
        ensureNotClosed();
        if (status != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Jobs can be completed only in IN_PROGRESS status, current status: "
                    + status);
        }
        if (index < 0 || index >= jobs.size()) {
            throw new IllegalArgumentException("Job with index " + index + " does not exist");
        }
        jobs.get(index).markDone();
    }

    public void complete() {
        ensureNotClosed();
        boolean allDone = jobs.stream()
                .allMatch(Job::isDone);
        if (!allDone) {
            throw new IllegalStateException("Cannot complete order #" + id + ": some jobs are not done");
        }
        changeStatus(OrderStatus.COMPLETED);
        releaseMechanic();
    }

    public void cancel() {
        changeStatus(OrderStatus.CANCELLED);
        releaseMechanic();
    }

    public BigDecimal getPartsCost() {
        return jobs.stream()
                .map(Job::getPartsCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getLaborCost() {
        return jobs.stream()
                .map(Job::getLaborCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPartsDiscount() {
        return discount(getPartsCost(), car.getOwner().getPartsDiscountPercent());
    }

    public BigDecimal getLaborDiscount() {
        return discount(getLaborCost(), car.getOwner().getLaborDiscountPercent());
    }

    public BigDecimal getTotalCost() {
        return getPartsCost().add(getLaborCost())
                .subtract(getPartsDiscount())
                .subtract(getLaborDiscount());
    }

    private static BigDecimal discount(BigDecimal amount, BigDecimal percent) {
        return amount.multiply(percent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public boolean isActive() {
        return !status.isClosed();
    }

    private void changeStatus(OrderStatus next) {
        ensureNotClosed();
        if (!status.canTransitionTo(next)) {
            throw new IllegalStateException("Forbidden status transition: " + status + " -> " + next);
        }
        status = next;
    }

    private void ensureCanDo(Mechanic m, Job job) {
        if (!m.canDo(job)) {
            throw new IllegalStateException("Mechanic " + m.getFullName() + " cannot do job \"" + job.getDescription()
                    + "\": requires " + job.getSpecialization());
        }
    }

    private void ensureNotClosed() {
        if (status.isClosed()) {
            throw new IllegalStateException("Order #" + id + " is " + status + " and cannot be changed");
        }
    }

    private void releaseMechanic() {
        if (mechanic != null) {
            mechanic.release();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        Order other = (Order) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order #" + id
                + " [" + status + "] "
                + car.getMake() + " " + car.getModel()
                + ", mechanic: " + (mechanic == null ? "not assigned" : mechanic.getFullName())
                + ", jobs: " + jobs.size()
                + ", cost: " + getTotalCost() + " UAH";
    }
}
