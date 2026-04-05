/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 2: Pizza Builder                                          ║
 * ║  Immutable Pizza with fluent Builder, validation, and presets       ║
 * ║                                                                      ║
 * ║  ASCII FLOW:                                                         ║
 * ║                                                                      ║
 * ║   Pizza.builder()                                                    ║
 * ║     .size("L")          → returns Builder (this)                    ║
 * ║     .crust("thick")     → returns Builder (this)                    ║
 * ║     .topping("Pepperoni")→ returns Builder (this)                   ║
 * ║     .extraCheese(true)  → returns Builder (this)                    ║
 * ║     .build()            → validates, constructs immutable Pizza      ║
 * ║                                                                      ║
 * ║   Pizza fields:  ALL final — cannot be mutated after construction    ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates the Builder pattern with immutability, validation, and fluent API.
 * Pattern analogues in Spring: ResponseEntity builder, UriComponentsBuilder.
 */
public class Sol02_PizzaBuilder {

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 1: Product — immutable Pizza with private constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Immutable Pizza — all fields final, only accessible via builder.
     * Immutability prevents accidental mutation after construction.
     */
    static class Pizza {

        // WHY final: enforces immutability — JVM disallows reassignment after construction
        private final String size;
        private final String crust;
        private final List<String> toppings;   // unmodifiable copy
        private final boolean extraCheese;

        /**
         * Private constructor — only Builder can call this.
         * Takes a fully validated Builder to populate fields.
         *
         * @param builder validated Builder instance
         */
        private Pizza(Builder builder) {
            this.size = builder.size;
            this.crust = builder.crust != null ? builder.crust : "thin";  // default crust
            // WHY unmodifiableList: prevents external code from mutating the list reference
            this.toppings = Collections.unmodifiableList(new ArrayList<>(builder.toppings));
            this.extraCheese = builder.extraCheese;
        }

        /** @return human-readable pizza description */
        @Override
        public String toString() {
            return String.format("Pizza[%s, %s crust, %s, %s]",
                size,
                crust,
                toppings,
                extraCheese ? "extra cheese" : "no extra cheese"
            );
        }

        /** Factory method to start building a pizza. */
        public static Builder builder() {
            return new Builder();
        }

        // ─────────────────────────────────────────────────────────────────────
        // LAYER 2: Builder — mutable accumulator with fluent setters
        // ─────────────────────────────────────────────────────────────────────

        /**
         * Fluent builder for Pizza construction.
         * Validates required fields and builds immutable Pizza.
         */
        static class Builder {

            // WHY private non-final: builder fields accumulate state before build()
            private String size;
            private String crust;
            private List<String> toppings = new ArrayList<>();
            private boolean extraCheese = false;

            // WHY presets map: encapsulates known pizza configurations
            private static final Map<String, Runnable> PRESETS_PLACEHOLDER = Map.of();

            /**
             * Sets pizza size. Required — must call before build().
             *
             * @param size "S", "M", or "L"
             * @return this Builder for chaining
             */
            public Builder size(String size) {
                this.size = size;
                return this;   // WHY return this: enables method chaining (fluent API)
            }

            /**
             * Sets crust style. Optional — defaults to "thin".
             *
             * @param crust "thin" or "thick"
             * @return this Builder for chaining
             */
            public Builder crust(String crust) {
                this.crust = crust;
                return this;
            }

            /**
             * Adds a topping. Can be called multiple times.
             *
             * @param topping topping name
             * @return this Builder for chaining
             */
            public Builder topping(String topping) {
                this.toppings.add(topping);
                return this;
            }

            /**
             * Enables extra cheese.
             *
             * @param extraCheese true to add extra cheese
             * @return this Builder for chaining
             */
            public Builder extraCheese(boolean extraCheese) {
                this.extraCheese = extraCheese;
                return this;
            }

            /**
             * Applies a preset configuration to this builder.
             * Demonstrates how presets layer on top of the fluent API.
             *
             * @param presetName "margherita" or "hawaiian"
             * @return this Builder for chaining
             */
            public Builder preset(String presetName) {
                return switch (presetName.toLowerCase()) {
                    case "margherita" -> this
                        .crust("thin")
                        .topping("Mozzarella")
                        .topping("Tomato")
                        .topping("Basil");
                    case "hawaiian" -> this
                        .crust("thick")
                        .topping("Ham")
                        .topping("Pineapple")
                        .extraCheese(true);
                    default -> throw new IllegalArgumentException(
                        "Unknown preset: " + presetName
                    );
                };
            }

            /**
             * Validates state and constructs the immutable Pizza.
             *
             * @return new immutable Pizza instance
             * @throws IllegalStateException if required fields are missing
             */
            public Pizza build() {
                // WHY validate in build(): fail fast at construction boundary,
                // not deep inside business logic where NPE cause is unclear
                if (size == null || size.isBlank()) {
                    throw new IllegalStateException("Pizza size is required (S, M, or L)");
                }
                if (toppings.isEmpty()) {
                    throw new IllegalStateException("At least one topping is required");
                }
                return new Pizza(this);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 3: Main — demonstrates builder usage and validation
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Custom pizza with all options
        Pizza custom = Pizza.builder()
            .size("L")
            .crust("thick")
            .topping("Pepperoni")
            .topping("Mushrooms")
            .topping("Olives")
            .extraCheese(true)
            .build();
        System.out.println(custom);

        // Preset pizza with explicit size
        Pizza margherita = Pizza.builder()
            .size("M")
            .preset("margherita")
            .build();
        System.out.println(margherita);

        // Validation: no size → exception
        try {
            Pizza.builder()
                .topping("Cheese")
                .build();
        } catch (IllegalStateException e) {
            System.out.println("Expected: " + e.getMessage());
        }

        // Validation: no toppings → exception
        try {
            Pizza.builder()
                .size("S")
                .build();
        } catch (IllegalStateException e) {
            System.out.println("Expected: " + e.getMessage());
        }
    }
}

/* ─────────────────────────────────────────────────────────────────────────────
   COMMON MISTAKES

   MISTAKE 1: Public constructor instead of private
     public Pizza(String size, ...) ← defeats the Builder; bypasses validation
     Always private constructor — force clients through Builder.

   MISTAKE 2: Mutable toppings list returned
     this.toppings = builder.toppings;  ← caller still holds reference, can mutate
     Always wrap: Collections.unmodifiableList(new ArrayList<>(builder.toppings))

   MISTAKE 3: Setters not returning 'this'
     public void size(String s) { this.size = s; }  ← no chaining possible
     Always: return this;

   MISTAKE 4: Validating in individual setters instead of build()
     public Builder size(String s) { if (s == null) throw ...; }
     You lose the ability to partially construct and pass the builder around.
     Validate only in build() where you have full context.

   MISTAKE 5: Not copying the toppings list
     this.toppings = builder.toppings;  ← Pizza and Builder share the same list
     If builder is reused, Pizza's toppings change!
     Always copy: new ArrayList<>(builder.toppings)
   ───────────────────────────────────────────────────────────────────────────── */
