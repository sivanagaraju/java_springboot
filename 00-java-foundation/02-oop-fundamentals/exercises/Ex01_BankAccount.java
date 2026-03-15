public class Ex01_BankAccount {
    
    // TODO 1: Create a class called "BankAccount" right above the Ex01_BankAccount class 
    // (do not make it public, otherwise it must have its own file).
    
    // Inside BankAccount:
    // TODO 2: Create a private String field called 'accountNumber'
    // TODO 3: Create a private double field called 'balance'
    
    // TODO 4: Create a parameterized constructor that initializes both fields.
    // If the provided balance is less than 0, initialize it to 0.0 instead.

    // TODO 5: Create a public getter for 'accountNumber'. Do NOT create a setter for it.
    // TODO 6: Create a public getter for 'balance'. DO NOT create a raw setter for it.

    // TODO 7: Create a public method 'deposit(double amount)'. 
    // It should add the amount to the balance ONLY if the amount is greater than 0.

    // TODO 8: Create a public method 'withdraw(double amount)'.
    // It should deduct the amount ONLY if the amount is greater than 0 AND there are sufficient funds.
    // If successful, return true. Otherwise, return false.

    public static void main(String[] args) {
        System.out.println("--- Starting Encapsulation Test ---");
        
        // UNCOMMENT the following code to test your implementation
        

        /*
        BankAccount account = new BankAccount("AC123456", 500.00);
        
        System.out.println("Account Number: " + account.getAccountNumber()); // Expected: AC123456
        System.out.println("Initial Balance: " + account.getBalance());      // Expected: 500.0
        
        account.deposit(150.00);
        System.out.println("Balance after deposit: " + account.getBalance()); // Expected: 650.0
        
        account.deposit(-50.00); // Should be ignored
        System.out.println("Balance after bad deposit: " + account.getBalance()); // Expected: 650.0
        
        boolean success1 = account.withdraw(100.00);
        System.out.println("Withdraw 100? " + success1 + " | Balance: " + account.getBalance()); // Expected: true | 550.0
        
        boolean success2 = account.withdraw(9999.00);
        System.out.println("Withdraw 9999? " + success2 + " | Balance: " + account.getBalance()); // Expected: false | 550.0

        // Security Test: The following lines should NOT compile if uncommented:
        // account.balance = 5000000;
        // account.accountNumber = "HACKED";
        */
        
        System.out.println("If the code above runs successfully and tests match expectations, you pass!");
    }
}
