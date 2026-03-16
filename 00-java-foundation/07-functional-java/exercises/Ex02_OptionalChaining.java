/**
 * ====================================================================
 * FILE    : Ex02_OptionalChaining.java
 * MODULE  : 07 — Functional Java
 * PURPOSE : Practice null-safe navigation with Optional
 * ====================================================================
 *
 * EXERCISES:
 *
 *   Given a chain: User → Profile → Address → City,
 *   where any level could be null/empty:
 *
 *   1. getUserCity(userId) → String
 *      - Navigate the full chain using Optional.flatMap()
 *      - Return the city name or "Unknown" if any step is null
 *
 *   2. getUserDisplayName(userId) → String
 *      - Return profile nickname if present
 *      - Else return user's name
 *      - Else return "Anonymous"
 *      - HINT: Use Optional.or() (Java 9+) or orElse chains
 *
 *   3. processValidUsers(userIds) → List<String>
 *      - From a list of user IDs, get only valid city names
 *      - HINT: Use flatMap(Optional::stream)
 *
 * CHAINING PATTERN:
 *
 *   ┌────────┐     ┌─────────┐     ┌─────────┐     ┌──────┐
 *   │ userId │────▶│  User?  │────▶│ Profile?│────▶│City? │
 *   └────────┘     └─────────┘     └─────────┘     └──────┘
 *   findUser()     getProfile()    getAddress()     getCity()
 *   Opt<User>      Opt<Profile>    Opt<Address>     Opt<String>
 *
 *   findUser(id)
 *       .flatMap(User::getProfile)
 *       .flatMap(Profile::getAddress)
 *       .map(Address::getCity)
 *       .orElse("Unknown")
 *
 * ====================================================================
 */
import java.util.*;

public class Ex02_OptionalChaining {

    // ── Domain model (each level can be absent) ─────────────────────
    record Address(String city, String zipCode) {}

    record Profile(String nickname, Address address) {
        Optional<String> getNickname() { return Optional.ofNullable(nickname); }
        Optional<Address> getAddress() { return Optional.ofNullable(address); }
    }

    record User(String name, Profile profile) {
        Optional<Profile> getProfile() { return Optional.ofNullable(profile); }
    }

    // ── Simulated database ──────────────────────────────────────────
    static final Map<String, User> DB = Map.of(
            "U001", new User("Alice", new Profile("ally", new Address("Seattle", "98101"))),
            "U002", new User("Bob",   new Profile(null, new Address("Portland", "97201"))),
            "U003", new User("Charlie", new Profile("chuck", null)),
            "U004", new User("Diana", null)
    );

    static Optional<User> findUser(String id) {
        return Optional.ofNullable(DB.get(id));
    }

    // TODO: Navigate User → Profile → Address → City using Optional chain
    public static String getUserCity(String userId) {
        // HINT: findUser(id).flatMap(...).flatMap(...).map(...).orElse(...)
        return "Unknown";
    }

    // TODO: Return nickname if present, else user's name, else "Anonymous"
    public static String getUserDisplayName(String userId) {
        // HINT: Use flatMap for profile, then flatMap for nickname,
        //       with fallback to user's name
        return "Anonymous";
    }

    // TODO: From list of IDs, get cities of valid users (skip nulls)
    public static List<String> processValidUsers(List<String> userIds) {
        // HINT: Stream + map to Optional + flatMap(Optional::stream)
        return List.of();
    }

    public static void main(String[] args) {
        System.out.println("=== Optional Chaining Exercise ===\n");

        for (String id : List.of("U001", "U002", "U003", "U004", "U999")) {
            System.out.println(id + " city: " + getUserCity(id));
            System.out.println(id + " display: " + getUserDisplayName(id));
        }

        System.out.println("\nValid cities: " +
                processValidUsers(List.of("U001", "U002", "U003", "U004", "U999")));
    }
}
