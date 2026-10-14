import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The car service: stores registered customers, cars, mechanics and orders
 * and creates new objects with unique identifiers.
 */
public class Storage {

    private String name;
    private final List<Customer> customers = new ArrayList<>();
    private final Map<String, Car> carsByVin = new HashMap<>();
    private final List<Mechanic> mechanics = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    private long nextCustomerId = 1;
    private long nextMechanicId = 1;
    private long nextOrderId = 1;

    public Storage(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validation.requireText(name, "Service name");
    }

    public Customer registerCustomer(String fullName, String phone) {
        Customer customer = new Customer(nextCustomerId, fullName, phone);
        nextCustomerId++;
        customers.add(customer);
        return customer;
    }

    public Car registerCar(String vin, String make, String model, int year, Customer owner) {
        if (!customers.contains(owner)) {
            throw new IllegalArgumentException("The car owner is not registered in the service");
        }
        Car car = new Car(vin, make, model, year, owner);
        if (carsByVin.containsKey(car.getVin())) {
            throw new IllegalArgumentException("A car with VIN " + car.getVin() + " is already registered");
        }
        carsByVin.put(car.getVin(), car);
        return car;
    }

    public Mechanic hireMechanic(String fullName, String specialization) {
        Mechanic mechanic = new Mechanic(nextMechanicId, fullName, specialization);
        nextMechanicId++;
        mechanics.add(mechanic);
        return mechanic;
    }

    public Order createOrder(Car car) {
        Validation.requireNonNull(car, "Car");
        if (!car.equals(carsByVin.get(car.getVin()))) {
            throw new IllegalArgumentException("Car " + car.getVin() + " is not registered in the service");
        }
        Order order = new Order(nextOrderId, car);
        nextOrderId++;
        orders.add(order);
        return order;
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public List<Car> getCars() {
        return List.copyOf(carsByVin.values());
    }

    public List<Mechanic> getMechanics() {
        return Collections.unmodifiableList(mechanics);
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }
}
