package exercises;

// Pretend SDK Imports
@interface Entity {}
@interface Table { String name(); }
@interface Id {}
@interface GeneratedValue { String strategy(); }
@interface Column { String name() default ""; boolean unique() default false; boolean nullable() default true; }

/**
 * EXERCISE 1: JPA Entity Mapping
 * 
 * TASK:
 * 1. Mark this class properly as a mapped Database Entity.
 * 2. Manually override the exact physical table name to be "inventory_products".
 * 3. Mark the 'id' field as the absolute Primary Key.
 * 4. Configure the ID to auto-increment explicitly using "IDENTITY".
 * 5. Configure the 'sku' field to map to a column named "product_sku", unique, and logically NOT NULL.
 */

// TODO: 1 & 2. Add Class-level annotations here
public class Ex01_EntityMapping {

    // TODO: 3 & 4. Primary Key Mapping
    private Long id;

    // TODO: 5. Custom Column Rules
    private String sku;

    private String title;
    private double price;

    // Hibernate mandates this natively!
    protected Ex01_EntityMapping() {}

    public Ex01_EntityMapping(String sku, String title, double price) {
        this.sku = sku;
        this.title = title;
        this.price = price;
    }
    
    // Getters and Setters omitted for brevity
}
