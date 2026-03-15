import build_repo
paths = build_repo.parse_structure('spring-learning-repo-structure.md')
print(f"Total paths parsed: {len(paths)}")
print("First 10:")
for p in paths[:10]: print(p[0])
print("Last 10:")
for p in paths[-10:]: print(p[0])
