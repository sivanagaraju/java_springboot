/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_OptionalChaining.java                            ║
 * ║  MODULE : 00-java-foundation / 07-functional-java                ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — null-safe navigation with Optional  ║
 * ║  DEMONSTRATES   : flatMap chains, orElse, or(), Optional::stream ║
 * ║  PYTHON COMPARE : optional chaining (?.) vs Optional.flatMap     ║
 * ║                                                                  ║
 * ║  NAVIGATION CHAIN:                                               ║
 * ║   userId → Optional<User> → Optional<Profile> → Optional<City>  ║
 * ║   flatMap unwraps each Optional before the next step             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Solutions for Optional chaining exercises.
 *
 * <p>Key pattern: use {@code flatMap} when the mapping function itself returns
 * an {@code Optional}. Use {@code map} when the mapping function returns a
 * plain value. Mixing them up produces {@code Optional<Optional<T>>} — a common bug.
 *
 * <p>Python analogy: Python 3.10+ has the optional chaining operator
 * ({@code user?.profile?.address?.city}). Java achieves the same null-safety
 * explicitly via Optional chains — more verbose but compile-time verified.
 */
public class Sol02_OptionalChaining {

    // ── Domain Model ─────────────────────────────────────────────────

    record Address(String city, String zipCode) {}

    record Profile(String nickname, Address address) {
        // WHY Optional return: nickname may be absent (null)
        Optional<String> getNickname() { return Optional.ofNullable(nickname); }
        // WHY Optional return: address may be absent
        Optional<Address> getAddress()  { return Optional.ofNullable(address); }
    }

    record User(String name, Profile profile) {
        Optional<Profile> getProfile() { return Optional.ofNullable(profile); }
    }

    // ── Simulated Database ───────────────────────────────────────────

    static final Map<String, User> DB = Map.of(
            "U001", new User("Alice",   new Profile("ally",  new Address("Seattle",  "98101"))),
            "U002", new User("Bob",     new Profile(null,    new Address("Portland", "97201"))),
            "U003", new User("Charlie", new Profile("chuck", null)),
            "U004", new User("Diana",   null)
    );

    static Optional<User> findUser(String id) {
        // WHY ofNullable: DB.get() returns null for unknown keys
        return Optional.ofNullable(DB.get(id));
    }

    /**
     * Returns the city of the user's address, or "Unknown" if any step is absent.
     *
     * <p>Chain: {@code findUser → getProfile → getAddress → city}
     *
     * <p>WHY flatMap for each Optional-returning step: {@code map(User::getProfile)}
     * would produce {@code Optional<Optional<Profile>>}. {@code flatMap} flattens
     * the inner Optional — producing just {@code Optional<Profile>}.
     *
     * <p>WHY map for city (last step): {@code Address::city} returns a plain String,
     * not an Optional — so map is correct here.
     *
     * @param userId the user ID to look up
     * @return city name or "Unknown"
     */
    public static String getUserCity(String userId) {
        return findUser(userId)
                .flatMap(User::getProfile)      // Optional<User> → Optional<Profile>
                .flatMap(Profile::getAddress)   // Optional<Profile> → Optional<Address>
                .map(Address::city)             // Optional<Address> → Optional<String>
                .orElse("Unknown");             // Optional<String> → String
    }

    /**
     * Returns the user's display name with priority: nickname > real name > "Anonymous".
     *
     * <p>WHY flatMap for profile: profile returns Optional, not plain Profile.
     * WHY flatMap for nickname: getNickname() returns Optional, not plain String.
     * WHY or() (Java 9+): provides a fallback Optional when nickname is absent —
     * different from orElse which returns a plain value.
     *
     * @param userId the user ID to look up
     * @return display name with fallback chain
     */
    public static String getUserDisplayName(String userId) {
        return findUser(userId)
                .flatMap(user ->
                        // WHY flatMap here: we want Optional<String>, not Optional<Optional<String>>
                        user.getProfile()
                            .flatMap(Profile::getNickname) // prefer nickname
                            .or(() -> Optional.of(user.name())) // WHY or(): fallback to name as Optional
                )
                .orElse("Anonymous"); // final fallback if user not found at all
    }

    /**
     * Filters a list of user IDs to only those with known cities.
     *
     * <p>WHY {@code flatMap(Optional::stream)}: converts each Optional to a stream of
     * 0 or 1 elements — empty Optional becomes empty stream (filtered out),
     * present Optional becomes a single-element stream (included).
     * This is the idiomatic way to filter/map with Optional in stream pipelines.
     *
     * <p>Python: {@code [city for uid in ids if (city := get_city(uid)) != 'Unknown']}
     *
     * @param userIds list of user IDs to process
     * @return list of non-null city names
     */
    public static List<String> processValidUsers(List<String> userIds) {
        return userIds.stream()
                .map(id -> findUser(id)
                        .flatMap(User::getProfile)
                        .flatMap(Profile::getAddress)
                        .map(Address::city)) // Stream<Optional<String>>
                // WHY flatMap(Optional::stream): collapses empty Optionals,
                // keeps only present values — clean alternative to filter+get
                .flatMap(Optional::stream) // Stream<String>
                .collect(Collectors.toList());
    }

    /**
     * Runs all test cases.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("=== Optional Chaining Solutions ===\n");

        System.out.println("--- getUserCity ---");
        for (String id : List.of("U001", "U002", "U003", "U004", "U999")) {
            System.out.printf("  %s → %s%n", id, getUserCity(id));
        }
        // Expected:
        // U001 → Seattle   (has full chain)
        // U002 → Portland  (no nickname but has address)
        // U003 → Unknown   (no address)
        // U004 → Unknown   (no profile)
        // U999 → Unknown   (user not found)

        System.out.println("\n--- getUserDisplayName ---");
        for (String id : List.of("U001", "U002", "U003", "U004", "U999")) {
            System.out.printf("  %s → %s%n", id, getUserDisplayName(id));
        }
        // Expected:
        // U001 → ally     (has nickname)
        // U002 → Bob      (no nickname → fallback to name)
        // U003 → chuck    (has nickname)
        // U004 → Diana    (no profile but user exists → name)
        // U999 → Anonymous (user not found)

        System.out.println("\n--- processValidUsers ---");
        List<String> cities = processValidUsers(
                List.of("U001", "U002", "U003", "U004", "U999"));
        System.out.println("  Cities: " + cities);
        // Expected: [Seattle, Portland] — only U001 and U002 have full address chains
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using map() instead of flatMap() for Optional-returning methods
 *   WRONG: findUser(id).map(User::getProfile)   → Optional<Optional<Profile>>
 *   RIGHT: findUser(id).flatMap(User::getProfile) → Optional<Profile>
 *   Rule: if the mapping function returns Optional<T>, use flatMap.
 *          if it returns T (plain value), use map.
 *
 * MISTAKE 2: Optional.get() without isPresent()
 *   WRONG: findUser(id).get().getProfile()  // NoSuchElementException if absent
 *   RIGHT: findUser(id).flatMap(...).orElse(default)
 *
 * MISTAKE 3: orElse vs or()
 *   orElse(T): returns a plain T value. Cannot chain another Optional.
 *   or(Supplier<Optional<T>>): returns another Optional. Use for fallback chains.
 *   WRONG: .or(() -> user.name())  // doesn't compile — name() returns String
 *   RIGHT: .or(() -> Optional.of(user.name()))
 *
 * MISTAKE 4: Using Optional as a field or method parameter
 *   WRONG: class User { Optional<Profile> profile; }  // Bloch Item 55 violation
 *   WRONG: void process(Optional<User> user)          // unnecessary overhead
 *   RIGHT: Optional only as method RETURN type for potentially-absent results.
 *
 * MISTAKE 5: Optional.of() with a potentially null value
 *   WRONG: Optional.of(possiblyNull)   // NullPointerException if null
 *   RIGHT: Optional.ofNullable(possiblyNull)  // returns empty Optional if null
 *   Use Optional.of() only when you are CERTAIN the value is non-null.
 * ═══════════════════════════════════════════════════════════════════ */
