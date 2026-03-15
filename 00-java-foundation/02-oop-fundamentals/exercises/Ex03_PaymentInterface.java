public class Ex03_PaymentInterface {

    // TODO 1: Create an interface named 'Payable'
    // Define an abstract method: boolean authorize(double amount);
    // Define an abstract method: boolean capture();

    // TODO 2: Create a concrete class 'CreditCard' that implements 'Payable'
    // Add private fields for 'cardNumber' and 'cvv'.
    // Create a constructor to initialize them.
    // Implement authorize() to just print "Authorizing card..." and return true.
    // Implement capture() to just print "Capturing funds from credit network..." and return true.

    // TODO 3: Create a concrete class 'UPI' that implements 'Payable'
    // Add a private field for 'vpa' (Virtual Payment Address, e.g., user@bank).
    // Create a constructor to initialize it.
    // Implement authorize() to print "Sending UPI PIN request to " + vpa and return true.
    // Implement capture() to print "Waiting for bank confirmation..." and return true.

    public static void main(String[] args) {
        System.out.println("--- Starting Interfaces Test ---");
        
        // UNCOMMENT the following code to test your implementation
        
        /*
        // Creating concrete objects
        CreditCard myCard = new CreditCard("1111-2222-3333-4444", 123);
        UPI myUPI = new UPI("sivan@icici");

        // Treating them solely by their Capabilities (Interfaces)
        System.out.println("--- Checkout Flow ---");
        boolean isCardOk = checkout(myCard, 99.99);
        System.out.println("Card Checkout successful: " + isCardOk);

        System.out.println("\n--- Alternative Flow ---");
        boolean isUpiOk = checkout(myUPI, 50.00);
        System.out.println("UPI Checkout successful: " + isUpiOk);
        */

        System.out.println("If the code above runs successfully and tests match expectations, you pass!");
    }

    // TODO 4: Notice this method. It takes the INTERFACE type, not the concrete classes!
    // Uncomment this method after defining the interface.
    /*
    public static boolean checkout(Payable paymentMethod, double amount) {
        System.out.println("Commencing checkout gateway...");
        if (paymentMethod.authorize(amount)) {
            return paymentMethod.capture();
        }
        return false;
    }
    */
}
