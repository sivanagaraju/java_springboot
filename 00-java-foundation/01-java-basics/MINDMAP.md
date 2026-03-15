# Java Basics

## `00-java-foundation/01-java-basics`

### `explanation/`
- **[`01-how-java-works.md`](explanation/01-how-java-works.md)** & **[`HowJavaWorks.java`](explanation/HowJavaWorks.java)**
  - Execution Model
    - javac (Compiler -> Bytecode `.class`)
    - JVM (Executer)
      - Class Loader -> Security Verify
      - Execution Engine
        - Interpreter
        - JIT Compiler (Hotspots -> Machine Code)
  - Environment
    - JDK (Development Kit: JRE + Tools)
    - JRE (Runtime: JVM + Libraries)
    - JVM (Virtual Machine)
  - Python Comparison: Script vs `public static void main`

- **[`02-variables-datatypes.md`](explanation/02-variables-datatypes.md)** & **[`VariablesDemo.java`](explanation/VariablesDemo.java)**
  - Primitives (Stack Memory, No Methods)
    - Numbers
      - Integers: `byte` (8-bit), `short` (16), `int` (32), `long` (64)
      - Decimals: `float` (32), `double` (64)
    - Text: `char` (16-bit Unicode)
    - Logic: `boolean` (1-bit)
  - Reference Types / Objects (Heap Memory)
    - Wrapper Classes (`Integer`, `Double`, `Boolean`, etc.)
    - `String`
  - The Autoboxing Trap (Implicit `null` unboxing -> `NullPointerException`)
  - Static Typing vs Python's Dynamic Objects

- **[`03-operators.md`](explanation/03-operators.md)** & **[`OperatorsDemo.java`](explanation/OperatorsDemo.java)**
  - Arithmetic
    - Integer division truncation (`5 / 2 = 2`)
    - Post/Pre-Increment (`x++`, `++x`)
  - Relational (`>`, `<`, `>=`, `<=`, `!=`)
  - Logical
    - AND: `&&`
    - OR: `||`
    - NOT: `!`
  - Ternary Expression (`condition ? trueVal : falseVal`)
  - The Equality Trap
    - Python `==` (Value) vs Java `==` (Identity/Memory Address)
    - Java `.equals()` (Object Value Equality)

- **[`04-control-flow.md`](explanation/04-control-flow.md)** & **[`ControlFlowDemo.java`](explanation/ControlFlowDemo.java)**
  - Block Scoping (`{}` vs Python indentation)
  - Strict Booleans (No "Truthiness", lists aren't false)
  - Conditionals
    - `if`, `else if`, `else`
    - Traditional `switch` (Requires `break`)
    - Modern Switch Expressions (Java 14+) -> `case X -> value; yield`
  - Loops
    - Standard `for (init; cond; step)`
    - Enhanced `for-each (Type item : items)`
    - `while` & `do-while`

### `exercises/`
- **[`README.md`](exercises/README.md)** (Execution Instructions)
- **[`Ex01_TypeConversion.java`](exercises/Ex01_TypeConversion.java)**
  - Implicit Widening (`int` -> `double`)
  - Explicit Narrowing Casts (`double` -> `int`)
  - Integer Overflow Traps
- **[`Ex02_Calculator.java`](exercises/Ex02_Calculator.java)**
  - Console Application
  - `Scanner(System.in)` Input
  - `while(running)` loop with `switch` expression calculation

### `MINDMAP.md`
- Visual Markdown Tree (Current File)
- Markmap extension compatible

### Interview QA Focus
- JDK vs JRE vs JVM
- Stack (Primitives) vs Heap (Objects)
- `==` vs `.equals()`
- JIT Compiler logic
- Implicit vs Explicit casting restrictions
