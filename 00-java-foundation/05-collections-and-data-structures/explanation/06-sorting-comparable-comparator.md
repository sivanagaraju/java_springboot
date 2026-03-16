# Sorting: Comparable vs. Comparator

Sorting domain objects mathematically requires defining order. Which object comes first?

Java provides two completely distinct interfaces to handle sorting.

## 1. `java.lang.Comparable<T>` (The Natural Order)

**Mechanics:**
- The domain class itself implements this interface.
- It defines the "default" sorting logic.
- You must override the `compareTo(T other)` method.

```java
public class User implements Comparable<User> {
    private int id;
    
    public User(int id) { this.id = id; }
    
    @Override
    public int compareTo(User other) {
        // Returns negative if this < other
        // Returns zero if this == other
        // Returns positive if this > other
        return Integer.compare(this.id, other.id);
    }
}
```

Now, `Collections.sort(userList)` or adding `User` objects to a `TreeSet` will automatically use this ID-based sorting.

## 2. `java.util.Comparator<T>` (The External Sorter)

**Mechanics:**
- Used when you need multiple different ways to sort, or you do not own the source code of the class you are sorting.
- It resides outside the domain class.
- You override `compare(T obj1, T obj2)`.

```java
public class UserAgeComparator implements Comparator<User> {
    @Override
    public int compare(User u1, User u2) {
        return Integer.compare(u1.getAge(), u2.getAge());
    }
}
```

With Java 8, this is heavily simplified via lambdas:
```java
userList.sort(Comparator.comparing(User::getAge));
```

## Architectural Summary
- **Comparable:** Defines intrinsic default sorting inside the class.
- **Comparator:** Defines extrinsic custom sorting injected from outside.
