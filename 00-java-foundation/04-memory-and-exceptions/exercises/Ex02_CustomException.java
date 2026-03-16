package exercises;

/**
 * EXERCISE 2: Custom Exception Modeling
 * 
 * TASK:
 * 1. Implement 'InvalidTransactionException' to extend RuntimeException.
 * 2. Add private fields 'transactionId' (String) and 'amount' (double).
 * 3. Provide a constructor allocating these fields and calling super(message).
 * 4. Expose getters.
 * 5. Complete 'PaymentProcessor.processPayment' to throw this exception if amount <= 0.
 * 6. Fix the Main method to catch it cleanly.
 */
public class Ex02_CustomException {

    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();

        try {
            processor.processPayment("TXN-100", 250.0);
            processor.processPayment("TXN-101", -50.0); // This should throw!
        } catch (InvalidTransactionException e) {
            System.out.println("Transaction Failed!");
            System.out.println("ID: " + e.getTransactionId());
            System.out.println("Amount: " + e.getAmount());
            System.out.println("Reason: " + e.getMessage());
        }
    }

    // TODO: Complete the Custom Exception
    public static class InvalidTransactionException extends RuntimeException {
        private String transactionId;
        private double amount;

        public InvalidTransactionException(String message, String transactionId, double amount) {
            super(message);
            this.transactionId = transactionId;
            this.amount = amount;
        }

        public String getTransactionId() { return transactionId; }
        public double getAmount() { return amount; }
    }

    public static class PaymentProcessor {
        public void processPayment(String txnId, double amount) {
            // TODO: Process validation
            if (amount <= 0) {
                throw new InvalidTransactionException("Amount cannot be negative or zero.", txnId, amount);
            }
            System.out.println("Processing " + txnId + " for $" + amount);
        }
    }
}
