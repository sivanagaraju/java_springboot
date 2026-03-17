# File I/O Basics — Byte Streams, Character Streams, and Resource Management

## 1. The Two Stream Families

```
┌────────────────────────────────────────────────────────────┐
│                 Java I/O Stream Hierarchy                    │
│                                                              │
│  BYTE STREAMS (raw data: images, PDFs, network)             │
│  ┌──────────────────┐     ┌───────────────────┐            │
│  │ InputStream       │     │ OutputStream       │            │
│  │  └─ FileInput     │     │  └─ FileOutput     │            │
│  │  └─ BufferedInput │     │  └─ BufferedOutput │            │
│  │  └─ DataInput     │     │  └─ DataOutput     │            │
│  └──────────────────┘     └───────────────────┘            │
│                                                              │
│  CHARACTER STREAMS (text: .txt, .csv, .json)                │
│  ┌──────────────────┐     ┌───────────────────┐            │
│  │ Reader            │     │ Writer             │            │
│  │  └─ FileReader    │     │  └─ FileWriter    │             │
│  │  └─ BufferedReader│     │  └─ BufferedWriter │            │
│  │  └─ InputStreamR  │     │  └─ OutputStreamW │            │
│  └──────────────────┘     └───────────────────┘            │
│                                                              │
│  Rule: Text = Reader/Writer | Binary = InputStream/Output    │
└────────────────────────────────────────────────────────────┘
```

---

## 2. Reading Files

### Byte-at-a-time (slow, but shows the mechanism)
```java
try (FileInputStream fis = new FileInputStream("data.bin")) {
    int byteRead;
    while ((byteRead = fis.read()) != -1) {  // -1 = EOF
        System.out.print((char) byteRead);
    }
}  // auto-closed here!
```

### Character-at-a-time
```java
try (FileReader reader = new FileReader("notes.txt")) {
    int ch;
    while ((ch = reader.read()) != -1) {
        System.out.print((char) ch);
    }
}
```

### Buffered line-by-line (what you'll actually use)
```java
try (BufferedReader br = new BufferedReader(new FileReader("log.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}
```

---

## 3. try-with-resources (Java 7+)

```
WITHOUT try-with-resources:              WITH try-with-resources:
┌──────────────────────────────┐        ┌──────────────────────────────┐
│ FileReader r = null;          │        │ try (FileReader r =          │
│ try {                         │        │      new FileReader("f.txt"))│
│   r = new FileReader("f.txt")│  →     │ {                            │
│   // use r                    │        │   // use r                   │
│ } finally {                   │        │ }  // auto-closed!           │
│   if (r != null) r.close();  │        │                              │
│ }                             │        │ // No finally needed!        │
└──────────────────────────────┘        └──────────────────────────────┘

Rule: Any class implementing AutoCloseable works with try-with-resources.
Spring's JdbcTemplate uses this internally for connections!
```

---

## 4. Writing Files

```java
// Overwrite (default)
try (FileWriter fw = new FileWriter("output.txt")) {
    fw.write("Hello, World!\n");
}

// Append mode
try (FileWriter fw = new FileWriter("log.txt", true)) {  // true = append
    fw.write("New log entry\n");
}

// Buffered writing (production code)
try (BufferedWriter bw = new BufferedWriter(new FileWriter("report.csv"))) {
    bw.write("Name,Age,City");
    bw.newLine();
    bw.write("John,25,NYC");
    bw.newLine();
}
```

---

## 🎯 Interview Questions

**Q1: InputStream vs Reader — when to use which?**
> `InputStream` reads raw bytes — use for binary data (images, PDFs, network). `Reader` reads characters with encoding — use for text (CSV, JSON, logs). Using `InputStream` for text can corrupt multi-byte characters (UTF-8).

**Q2: What happens if you don't close a stream?**
> Resource leak. File handles are limited per process (typically 1024-65535). Leaked handles cause "Too many open files" errors. `try-with-resources` guarantees cleanup even on exceptions.

**Q3: What does `AutoCloseable` require?**
> A single `close()` method. Any class implementing `AutoCloseable` can be used in try-with-resources. Spring's `JdbcTemplate` manages `Connection` (which is `AutoCloseable`) internally — you never explicitly close connections.
