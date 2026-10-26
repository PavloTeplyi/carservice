import java.math.BigDecimal;


public class CarServiceDemo {

    public static void main(String[] args) {
        Storage storage = new Storage("AutoMaster");

        printHeader("1. Registering a customer and a car");
        Customer customer = storage.registerCustomer("Ivan Petrenko", "+380671234567");
        Car car = storage.registerCar("WVWZZZ1JZXW000001", "Volkswagen", "Golf", 2015, customer);
        Car secondCar = storage.registerCar("JTDBR32E720000002", "Toyota", "Corolla", 2019, customer);
        System.out.println("Customer: " + customer);
        System.out.println("Car: " + car);
        System.out.println("Car: " + secondCar);

        customer.setPhone("+380509876543");
        System.out.println("Customer phone changed: " + customer);

        Mechanic oleh = storage.hireMechanic("Oleh Koval", Specialization.ENGINE, Specialization.BRAKES);
        Mechanic andrii = storage.hireMechanic("Andrii Melnyk", Specialization.SUSPENSION);
        System.out.println("Mechanics: " + oleh + ", " + andrii);

        expectError("Registering the same VIN again",
                () -> storage.registerCar("WVWZZZ1JZXW000001", "Skoda", "Octavia", 2018, customer));

        printHeader("2. Full order life cycle");
        Order order = storage.createOrder(car);
        printState(order);

        order.addJob(new Job("Oil change", Specialization.ENGINE, new BigDecimal("1200.00"), new BigDecimal("400.00")));
        order.addJob(new Job("Brake pad replacement", Specialization.BRAKES, new BigDecimal("1800.50"), new BigDecimal("650.00")));
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
        for (Job job : order.getJobs()) {
            System.out.println("  - " + job);
        }

        order.complete();
        printState(order);

        printHeader("3. Total cost");
        System.out.println("Amount due for order #" + order.getId() + ": " + order.getTotalCost() + " UAH");

        printHeader("4. Trying to approve an empty order");
        Order emptyOrder = storage.createOrder(secondCar);
        emptyOrder.diagnose();
        expectError("Approving an order without jobs", emptyOrder::approve);
        printState(emptyOrder);

        printHeader("5. Trying to start an order without a mechanic");
        Order noMechanicOrder = storage.createOrder(secondCar);
        noMechanicOrder.addJob(new Job("Wheel alignment", Specialization.SUSPENSION, BigDecimal.ZERO, new BigDecimal("700.00")));
        noMechanicOrder.diagnose();
        noMechanicOrder.approve();
        expectError("Starting without a mechanic", noMechanicOrder::start);
        printState(noMechanicOrder);

        expectError("Assigning a mechanic without the needed specialization",
                () -> noMechanicOrder.assignMechanic(oleh));

        printHeader("6. Trying to assign a busy mechanic");
        noMechanicOrder.assignMechanic(andrii);
        System.out.println("Andrii assigned to order #" + noMechanicOrder.getId());
        expectError("Assigning Andrii to another active order",
                () -> emptyOrder.assignMechanic(andrii));
        printState(emptyOrder);

        printHeader("7. Trying to change a completed order");
        expectError("Adding a job to a completed order",
                () -> order.addJob(new Job("Car wash", Specialization.WASHING, BigDecimal.ZERO, new BigDecimal("200.00"))));
        expectError("Cancelling a completed order", order::cancel);
        printState(order);

        printHeader("8. Trying a forbidden status transition");
        Order newOrder = storage.createOrder(car);
        expectError("Completing a just created order", newOrder::complete);
        printState(newOrder);

        printHeader("9. Cancelling frees the mechanic");
        noMechanicOrder.cancel();
        printState(noMechanicOrder);
        System.out.println("Andrii is busy: " + andrii.isBusy());
        emptyOrder.assignMechanic(andrii);
        System.out.println("Andrii assigned to order #" + emptyOrder.getId());
        printState(emptyOrder);

        printHeader("10. Corporate customer with separate labor and parts discounts");
        CorporateCustomer company = storage.registerCorporateCustomer("TransLogistics Ltd", "+380441112233",
                new BigDecimal("10"), new BigDecimal("5"));
        Car van = storage.registerCar("WV1ZZZ2KZAH000003", "Volkswagen", "Caddy", 2020, company);
        System.out.println("Customer: " + company);
        Order corporateOrder = storage.createOrder(van);
        corporateOrder.addJob(new Job("Oil change", Specialization.ENGINE, new BigDecimal("1200.00"), new BigDecimal("400.00")));
        corporateOrder.addJob(new Job("Brake pad replacement", Specialization.BRAKES, new BigDecimal("1800.50"), new BigDecimal("650.00")));
        System.out.println("  Parts: " + corporateOrder.getPartsCost()
                + ", discount -" + corporateOrder.getPartsDiscount());
        System.out.println("  Labor: " + corporateOrder.getLaborCost()
                + ", discount -" + corporateOrder.getLaborDiscount());
        System.out.println("  Total to pay: " + corporateOrder.getTotalCost() + " UAH");
        expectError("Discount above 100%", () -> company.setLaborDiscountPercent(new BigDecimal("120")));

        printHeader("All orders of service \"" + storage.getName() + "\"");
        for (Order o : storage.getOrders()) {
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

    private static void printState(Order order) {
        System.out.println("  -> " + order);
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
