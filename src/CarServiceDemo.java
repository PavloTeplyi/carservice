import java.math.BigDecimal;

/**
 * Demonstrates the model: positive and negative scenarios.
 */
public class CarServiceDemo {

    public static void main(String[] args) {
        Service service = new Service("AutoMaster");

        printHeader("1. Registering a customer and a car");
        Customer customer = service.registerCustomer("Ivan Petrenko", "+380671234567");
        Car car = service.registerCar("WVWZZZ1JZXW000001", "Volkswagen", "Golf", 2015, customer);
        Car secondCar = service.registerCar("JTDBR32E720000002", "Toyota", "Corolla", 2019, customer);
        System.out.println("Customer: " + customer);
        System.out.println("Car: " + car);
        System.out.println("Car: " + secondCar);

        customer.setPhone("+380509876543");
        System.out.println("Customer phone changed: " + customer);

        Mechanic oleh = service.hireMechanic("Oleh Koval", "engine");
        Mechanic andrii = service.hireMechanic("Andrii Melnyk", "suspension");
        System.out.println("Mechanics: " + oleh + ", " + andrii);

        expectError("Registering the same VIN again",
                () -> service.registerCar("WVWZZZ1JZXW000001", "Skoda", "Octavia", 2018, customer));

        printHeader("2. Full order life cycle");
        ServiceOrder order = service.createOrder(car);
        printState(order);

        order.addJob(new ServiceJob("Oil change", new BigDecimal("1200.00"), new BigDecimal("400.00")));
        order.addJob(new ServiceJob("Brake pad replacement", new BigDecimal("1800.50"), new BigDecimal("650.00")));
        System.out.println("Added two jobs");
        printState(order);

        order.diagnose();
        printState(order);

        order.approve();
        printState(order);

        order.assignMechanic(oleh);
        System.out.println("Assigned mechanic: " + oleh);
        printState(order);

        order.start();
        printState(order);

        order.completeJob(0);
        order.completeJob(1);
        for (ServiceJob job : order.getJobs()) {
            System.out.println("  - " + job);
        }

        order.complete();
        printState(order);

        printHeader("3. Total cost");
        System.out.println("Amount due for order #" + order.getId() + ": " + order.getTotalCost() + " UAH");

        printHeader("4. Trying to approve an empty order");
        ServiceOrder emptyOrder = service.createOrder(secondCar);
        emptyOrder.diagnose();
        expectError("Approving an order without jobs", emptyOrder::approve);
        printState(emptyOrder);

        printHeader("5. Trying to start an order without a mechanic");
        ServiceOrder noMechanicOrder = service.createOrder(secondCar);
        noMechanicOrder.addJob(new ServiceJob("Wheel alignment", BigDecimal.ZERO, new BigDecimal("700.00")));
        noMechanicOrder.diagnose();
        noMechanicOrder.approve();
        expectError("Starting without a mechanic", noMechanicOrder::start);
        printState(noMechanicOrder);

        printHeader("6. Trying to assign a busy mechanic");
        noMechanicOrder.assignMechanic(andrii);
        System.out.println("Andrii assigned to order #" + noMechanicOrder.getId());
        expectError("Assigning Andrii to another active order",
                () -> emptyOrder.assignMechanic(andrii));
        printState(emptyOrder);

        printHeader("7. Trying to change a completed order");
        expectError("Adding a job to a completed order",
                () -> order.addJob(new ServiceJob("Car wash", BigDecimal.ZERO, new BigDecimal("200.00"))));
        expectError("Cancelling a completed order", order::cancel);
        printState(order);

        printHeader("8. Trying a forbidden status transition");
        ServiceOrder newOrder = service.createOrder(car);
        expectError("Completing a just created order", newOrder::complete);
        printState(newOrder);

        printHeader("9. Cancelling frees the mechanic");
        noMechanicOrder.cancel();
        printState(noMechanicOrder);
        System.out.println("Andrii is busy: " + andrii.isBusy());
        emptyOrder.assignMechanic(andrii);
        System.out.println("Andrii assigned to order #" + emptyOrder.getId());
        printState(emptyOrder);

        printHeader("All orders of service \"" + service.getName() + "\"");
        for (ServiceOrder o : service.getOrders()) {
            System.out.println(o);
        }
    }

    private static void expectError(String description, Runnable action) {
        System.out.println("Attempt: " + description);
        try {
            action.run();
            System.out.println("  DEMO ERROR: an exception was expected, but the operation succeeded");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  Expected error: " + e.getMessage());
        }
    }

    private static void printState(ServiceOrder order) {
        System.out.println("  -> " + order);
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
