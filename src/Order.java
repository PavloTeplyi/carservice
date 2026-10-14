import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Order {

    private final long id;
    private final Car car;
    private final List<Job> jobs = new ArrayList<>();
    private Mechanic mechanic;
    private OrderStatus status;


    Order(long id, Car car) {
        this.id = id;
        this.car = Validation.requireNonNull(car, "Car");
        this.status = OrderStatus.CREATED;
    }

    public long getId() {
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
        newMechanic.occupy(this);
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

    public BigDecimal getTotalCost() {
        return jobs.stream()
                .map(Job::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
        return id == other.id;
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
