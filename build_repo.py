import os
import re

JAVA_TEMPLATE = """/**
 * ============================================================
 * FILE: {filename}
 * MODULE: {module}
 * ============================================================
 *
 * PURPOSE:
 *   [Auto-generated file]
 *   See explanation in the markdown files or project plan.
 *
 * HOW TO RUN:
 *   mvn compile
 *   mvn exec:java -Dexec.mainClass="..."
 * ============================================================
 */

// YOUR CODE HERE
"""

EXERCISE_TEMPLATE = """/**
 * ============================================================
 * EXERCISE: {filename}
 * MODULE: {module}
 * DIFFICULTY: [Auto-generated]
 * ============================================================
 *
 * SCENARIO:
 *   [Auto-generated exercise file]
 *
 * REQUIREMENTS:
 *   Follow the module's README or explanation files.
 *
 * HOW TO TEST:
 *   See testing instructions in the related module.
 * ============================================================
 */

// YOUR CODE HERE
"""

def parse_structure(filepath):
    lines = []
    with open(filepath, 'r', encoding='utf-8') as f:
        in_block = False
        for line in f:
            if line.startswith('```') and 'java' not in line:
                in_block = not in_block
                continue
            if in_block:
                lines.append(line.rstrip('\n'))

    paths = []
    stack = []

    for line in lines:
        if not line.strip() or line.strip() == 'spring-mastery/':
            continue

        match = re.match(r'^([│├└─\s]*)(.+?)(?:\s+←\s+(.*))?$', line)
        if match:
            prefix = match.group(1)
            name = match.group(2).strip()
            comment = match.group(3) if match.group(3) else ""

            visual_prefix = prefix.replace('├', ' ').replace('─', ' ').replace('│', ' ').replace('└', ' ')
            depth = (len(visual_prefix) // 4) - 1
            if depth < 0:
                depth = 0

            if name == '│':
                continue

            is_dir = name.endswith('/')
            clean_name = name.rstrip('/')

            while len(stack) > depth:
                stack.pop()

            stack.append(clean_name)

            full_path = os.path.join(*stack)
            paths.append((full_path, is_dir, comment.strip()))

    return paths

def build_repo(base_dir, paths):
    for path, is_dir, comment in paths:
        full_path = os.path.join(base_dir, path)

        # Skip top level files that we already created manually or are sensitive
        if len(path.split('/')) == 1 and not is_dir:
            continue

        # Also skip setup folder content as we did it manually
        if path.startswith('setup/'):
            continue

        if is_dir:
            os.makedirs(full_path, exist_ok=True)
        else:
            # Create parent dir if not exists
            os.makedirs(os.path.dirname(full_path), exist_ok=True)

            if full_path.endswith('.md'):
                # Markdown file
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        filename = os.path.basename(full_path)
                        f.write(f"# {filename}\n\n")
                        if comment:
                            f.write(f"> {comment}\n\n")
            elif full_path.endswith('.java'):
                # Java file
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        filename = os.path.basename(full_path)
                        # Extract module name from path (e.g., 05-spring-core)
                        parts = path.split('/')
                        module = parts[0] if len(parts) > 0 else "Unknown"
                        if len(parts) > 1 and parts[1] != 'explanation' and parts[1] != 'exercises':
                            module += f" / {parts[1]}"

                        if filename.startswith('Ex'):
                            f.write(EXERCISE_TEMPLATE.format(filename=filename, module=module))
                        else:
                            f.write(JAVA_TEMPLATE.format(filename=filename, module=module))
            elif full_path.endswith('.xml'):
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        f.write('<!-- auto-generated xml file -->\n<project>\n</project>')
            elif full_path.endswith('.yml'):
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        f.write('# auto-generated yml file\n')
            else:
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        f.write('// auto-generated file\n')

if __name__ == '__main__':
    paths = parse_structure('spring-learning-repo-structure.md')
    build_repo('spring-mastery', paths)
    print("Done building repository structure.")
