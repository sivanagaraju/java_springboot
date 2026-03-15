from build_repo import parse_structure
paths = parse_structure('spring-learning-repo-structure.md')
print("Total Paths parsed:", len(paths))
print("First 10:")
for p in paths[:10]: print(p[0])
print("Checking for anomalies...")
for p in paths:
    if "Topics" in p[0] or "annotationProcessor" in p[0] or "{" in p[0] or "java-" in p[0].split('/')[-1]:
        # we allow "00-java-foundation", but just check random suspicious string matches
        pass
    if "resources/interview-prep" in p[0] and "Questions" in p[0]:
        print("Anomaly:", p[0])
print("Done.")
