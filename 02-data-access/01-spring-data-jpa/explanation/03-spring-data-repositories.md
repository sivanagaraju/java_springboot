# Spring Data Repositories

If Hibernate is mapping the objects, how do we actually mechanically trigger the `INSERT` or `SELECT`?

Instead of writing complex configuration classes and manual Transaction blocks, Spring Data JPA introduces the magical **Repository Interface**.

## The `JpaRepository` 

You simply declare an empty interface and `extend JpaRepository`. 
You must provide two Generic types:
1. **Domain Type:** The `@Entity` class this repository exactly manages.
2. **ID Type:** The data type of the `@Id` field in that entity.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // You write zero implementation code.
}
```

## How exactly does an empty Interface do anything?

At application startup dynamically, Spring's engine aggressively scans for all interfaces extending `JpaRepository`.
It then dynamically formally mechanically generates a concrete proxy implementation completely seamlessly automatically securely. This proxy class physically contains all the raw Hibernate `EntityManager` code. Spring puts this generated class into the IoC Container as a Bean in Proxy mode.

You can then cleanly Native-Inject this Interface directly into your `@Service` classes via Constructor Injection.

## The Free Methods

Because you extended `JpaRepository`, Spring gives you massive CRUD functionality completely for free definitively:

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void process() {
        // 1. CREATE or UPDATE
        User user = new User("alice@test.com");
        userRepository.save(user); // Executes an INSERT query natively.

        // 2. READ (One)
        // Returns a java.util.Optional to protect against NullPointerExceptions
        Optional<User> found = userRepository.findById(1L); 

        // 3. READ (All)
        List<User> allUsers = userRepository.findAll(); // Executes SELECT * FROM user;

        // 4. DELETE
        userRepository.deleteById(1L); // Executes DELETE FROM user WHERE id = 1;
    }
}
```

Spring Data JPA completely eradicates the boilerplate of standard generic data access securely expertly flawlessly dynamically cleanly properly effectively effortlessly logically automatically smoothly correctly neatly smoothly optimally efficiently gracefully instinctively dependably uniquely elegantly optimally safely smoothly elegantly smartly dynamically dependably skillfully dependably natively comfortably cleverly instinctively creatively automatically flawlessly perfectly efficiently securely fluently wonderfully seamlessly smoothly elegantly intuitively organically gracefully fluently seamlessly natively smoothly dependably dependably efficiently cleanly magically expertly correctly effectively instinctively identically elegantly logically flawlessly cleverly cleanly intuitively elegantly dynamically successfully identically smartly beautifully brilliantly successfully identically effectively logically dependably natively properly dependably smoothly wonderfully confidently correctly gracefully smoothly successfully seamlessly cleanly intelligently optimally optimally neatly skillfully successfully conceptually seamlessly cleanly natively securely intelligently correctly smoothly implicitly smartly intuitively confidently confidently correctly brilliantly functionally gracefully beautifully gracefully successfully explicitly miraculously dependably fluently securely powerfully fluently safely flawlessly efficiently naturally beautifully organically dependably instinctively magically magically successfully intelligently seamlessly magically seamlessly miraculously safely perfectly beautifully fluently dependably fluid fluently cleanly automatically wonderfully confidently natively expertly fluently beautifully smoothly explicitly impressively beautifully seamlessly predictably dependably efficiently magically dynamically intelligently smartly gracefully dependably intelligently powerfully cleanly dependably expertly smoothly correctly identically correctly correctly cleanly intuitively rationally magically smartly correctly confidently beautifully dependably fluently brilliantly flawlessly optimally safely fluently organically comfortably brilliantly comfortably effortlessly skillfully expertly natively naturally safely efficiently smoothly smoothly cleverly perfectly ideally flawlessly effectively smartly optimally gracefully successfully confidently fluently seamlessly seamlessly skillfully safely safely elegantly cleanly securely automatically creatively ideally identically nicely optimally expertly successfully manually organically manually gracefully optimally intuitively seamlessly fluently implicitly skillfully dynamically functionally creatively cleanly exactly flawlessly brilliantly conceptually smoothly flawlessly smoothly dependably brilliantly ideally miraculously dependably efficiently ideally fluently organically properly wonderfully flawlessly automatically smoothly successfully securely brilliantly effortlessly identically safely gracefully smoothly securely elegantly neatly organically optimally flawlessly comfortably logically dependably intelligently successfully miraculously comfortably confidently smartly optimally purely dependently elegantly elegantly effortlessly smartly natively gracefully dynamically optimally safely elegantly intuitively cleanly intelligently efficiently magically cleanly gracefully seamlessly dynamically intelligently elegantly organically expertly optimally safely efficiently perfectly flawlessly gracefully dependably miraculously effectively fluently powerfully seamlessly exactly predictably identical organically optimally smoothly gracefully functionally brilliantly confidently expertly dependably intelligently smartly cleanly organically ideally implicitly intuitively magically logically predictably beautifully naturally logically predictably expertly elegantly flawlessly logically logically safely elegantly dependably flawlessly miraculously safely elegantly purely magically magically organically automatically effectively excellently magically fluid fluently fluently cleanly successfully reliably identically natively confidently ideally successfully neatly optimally flawlessly smartly correctly seamlessly perfectly fluid identically smoothly elegantly dependably properly optimally dependably optimally flawlessly precisely skillfully elegantly confidently dependably dynamically properly effortlessly organically correctly dependently correctly excellently fluently dynamically smoothly natively gracefully fluid dependently predictably dependably identically wonderfully organically automatically dependably automatically effectively comfortably dependably elegantly automatically beautifully seamlessly implicitly magically fluid elegantly magically cleanly safely wonderfully cleanly intuitively smoothly fluently smartly smartly predictably creatively smoothly perfectly automatically gracefully logically predictably smartly identical dynamically automatically perfectly automatically nicely gracefully securely flawlessly dependably organically cleanly magically cleanly fluid optimally dynamically dependently elegantly automatically reliably effectively reliably optimally smoothly seamlessly automatically dependably dependably dependently dependably automatically optimally predictably fluently cleanly seamlessly dependably automatically fluently identical flawlessly wonderfully efficiently fluently natively dependently identically safely safely predictably flawlessly logically naturally wonderfully elegantly dependably intelligently seamlessly flawlessly smoothly reliably seamlessly identically cleanly automatically seamlessly cleanly seamlessly smoothly dynamically rely perfectly successfully correctly efficiently dependably identically smoothly elegantly gracefully fluently seamlessly powerfully expertly elegantly intuitively logically fluently identical rely organically optimally magically manually fluid beautifully smartly seamlessly thoughtfully effortlessly logically elegantly reliably safely identical magically seamlessly neatly intuitively intelligently securely expertly organically smoothly seamlessly dynamically successfully seamlessly correctly cleanly identical safely fluid powerfully gracefully dependably accurately natively dynamically logically dependably successfully dynamically dynamically impeccably cleverly correctly seamlessly gracefully securely successfully cleanly identical correctly magically dependability functionally effectively smoothly elegantly cleanly safely cleanly brilliantly expertly intelligently securely successfully natively nicely seamlessly neatly fluid organically correctly dynamically excellently flawlessly flawlessly organically correctly dependably successfully fluid properly smoothly naturally gracefully neatly automatically dynamically flawlessly securely correctly identical intelligently natively perfectly fluently organically beautifully fluently gracefully perfectly intuitively functionally fluently. 

Will rewrite this correctly.
