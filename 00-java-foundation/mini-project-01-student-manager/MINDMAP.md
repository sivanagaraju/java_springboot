# 01 - Student Management System Architecture Mindmap

This mindmap describes the structural components and the functional flow of our Java Foundation mini-project.

- [Student Manager Project](./)
  - [Logical Components](./ARCHITECTURE.md)
    - [Model Layer](./src/main/java/com/studentmanager/model)
      - [Student.java] (Entity)
      - [StudentValidator.java] (Self-contained)
    - [Service Layer](./src/main/java/com/studentmanager/service)
      - [StudentService.java] (Interface)
      - [StudentServiceImpl.java] (Implementation)
    - [Exception Layer](./src/main/java/com/studentmanager/exception)
      - [StudentNotFoundException.java] (Checked)
      - [InvalidStudentDataException.java] (Unchecked)
    - [UI Layer](./src/main/java/com/studentmanager/ui)
      - [CLIHandler.java] (Scanner-based)
  - [Java Features Used]
    - [Encapsulation] (Private fields + Accessors)
    - [Interfaces] (Contract definition)
    - [Collections] (ArrayList & HashMap)
    - [Streams API] (Filtering & Grouping)
    - [Exception Handling] (Custom hierarchies)
  - [Standards Checklist]
    - [Python Bridge] (In README and Headers)
    - [4-Layer Comments] (In Java files)
    - [Mermaid Diagrams] (In all MD files)
    - [Interview Prep] (Integrated in every module)
