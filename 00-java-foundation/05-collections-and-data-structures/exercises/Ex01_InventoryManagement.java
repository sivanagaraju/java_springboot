package exercises;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * EXERCISE 1: Inventory Management
 * 
 * TASK:
 * 1. Initialize an ArrayList 'scanLog' and add items: "Apple", "Banana", "Apple", "Orange".
 * 2. Initialize a HashSet 'uniqueInventory'.
 * 3. Iterate over 'scanLog' and add every item into 'uniqueInventory'.
 * 4. Prove that 'uniqueInventory' has removed the duplicate "Apple".
 */
public class Ex01_InventoryManagement {

    public static void main(String[] args) {
        
        // TODO: 1. Create scanLog List and add items
        List<String> scanLog = new ArrayList<>();
        scanLog.add("Apple");
        scanLog.add("Banana");
        scanLog.add("Apple");
        scanLog.add("Orange");

        System.out.println("Raw Scan Log (Size " + scanLog.size() + "): " + scanLog);

        // TODO: 2 & 3. Create uniqueInventory Set and populate it
        Set<String> uniqueInventory = new HashSet<>(scanLog);

        // TODO: 4. Print the Set
        System.out.println("Unique Inventory (Size " + uniqueInventory.size() + "): " + uniqueInventory);
        
        // Output should show exactly 3 items. Order is not guaranteed.
    }
}
