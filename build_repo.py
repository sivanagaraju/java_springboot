import os
import re
from templates import JAVA_TEMPLATE, EXERCISE_TEMPLATE
from root_files import get_readme, get_implementation_plan, get_progress_tracker
from setup_script import get_verify_setup_sh

def parse_structure(filepath):
    lines = []
    with open(filepath, 'r', encoding='utf-8') as f:
        in_structure_block = False
        in_ignored_block = False
        for line in f:
            if line.startswith('```'):
                if in_structure_block:
                    in_structure_block = False
                elif in_ignored_block:
                    in_ignored_block = False
                else:
                    if 'java' in line or 'groovy' in line or 'bash' in line or 'mermaid' in line:
                        in_ignored_block = True
                    else:
                        in_structure_block = True
                continue

            if in_structure_block:
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

            if not any(char in prefix for char in ['├', '└', '│']):
                if not name.endswith('/') and not '.' in name:
                    continue
                if name.startswith('//') or name.startswith('*') or name.startswith('}') or name.startswith('---') or name.startswith('#'):
                    continue
                if 'import ' in name or 'package ' in name or '@' in name or 'return ' in name or '/**' in name:
                    continue
                if name.startswith('── '):
                    continue

            visual_prefix = prefix.replace('├', ' ').replace('─', ' ').replace('│', ' ').replace('└', ' ')

            if any(char in prefix for char in ['├', '└', '│']):
                depth = len(visual_prefix) // 4
            else:
                depth = 0

            if name == '│':
                continue

            is_dir = name.endswith('/')
            clean_name = name.rstrip('/')

            if "{" in clean_name or "}" in clean_name or "=" in clean_name or "implementation" in clean_name or "runtimeOnly" in clean_name:
                continue
            if clean_name.startswith('//') or clean_name.startswith('---') or clean_name.startswith('Every ') or clean_name.startswith('Topics:') or clean_name.startswith('NOTE:'):
                continue
            if ">" in clean_name:
                continue

            if depth > len(stack):
                depth = len(stack)

            while len(stack) > depth:
                stack.pop()

            if len(stack) > 0 and not is_dir and '.' in stack[-1]:
                stack.pop()
                depth -= 1

            if len(stack) > 0 and '.' in stack[-1]:
                stack.pop()

            stack.append(clean_name)

            full_path = os.path.join(*stack)
            paths.append((full_path, is_dir, comment.strip()))

    return paths

def create_root_files(base_dir):
    os.makedirs(base_dir, exist_ok=True)
    with open(os.path.join(base_dir, 'README.md'), 'w', encoding='utf-8') as f:
        f.write(get_readme())
    with open(os.path.join(base_dir, 'IMPLEMENTATION_PLAN.md'), 'w', encoding='utf-8') as f:
        f.write(get_implementation_plan())
    with open(os.path.join(base_dir, 'PROGRESS_TRACKER.md'), 'w', encoding='utf-8') as f:
        f.write(get_progress_tracker())

def get_package_name(module_string):
    clean = re.sub(r'[^a-zA-Z0-9/ ]', '', module_string)
    parts = [p.strip() for p in clean.split('/')]
    package_parts = []
    for p in parts:
        p = re.sub(r'^\d+\s*', '', p)
        package_parts.append(p.replace(' ', '').replace('-', '').lower())
    return ".".join(package_parts)

def build_repo(base_dir, paths):
    create_root_files(base_dir)
    for path, is_dir, comment in paths:
        full_path = os.path.join(base_dir, path)

        if len(path.split('/')) == 1 and not is_dir and path in ['README.md', 'IMPLEMENTATION_PLAN.md', 'PROGRESS_TRACKER.md']:
            continue

        if is_dir:
            os.makedirs(full_path, exist_ok=True)
        else:
            parent_dir = os.path.dirname(full_path)
            if parent_dir and not os.path.exists(parent_dir):
                if not os.path.isfile(parent_dir):
                    os.makedirs(parent_dir, exist_ok=True)
                else:
                    continue

            if full_path.endswith('.md'):
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        filename = os.path.basename(full_path)
                        f.write(f"# {filename}\n\n")
                        if comment:
                            f.write(f"> {comment}\n\n")
            elif full_path.endswith('.java'):
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        filename = os.path.basename(full_path)
                        classname = filename.replace('.java', '')
                        parts = path.split('/')
                        module = parts[0] if len(parts) > 0 else "Unknown"
                        if len(parts) > 1 and parts[1] != 'explanation' and parts[1] != 'exercises':
                            module += f" / {parts[1]}"

                        packagename = get_package_name(module)

                        if filename.startswith('Ex'):
                            f.write(EXERCISE_TEMPLATE.format(filename=filename, classname=classname, module=module, packagename=packagename))
                        else:
                            f.write(JAVA_TEMPLATE.format(filename=filename, classname=classname, module=module, packagename=packagename))
            elif full_path.endswith('.xml'):
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        f.write('<!-- auto-generated xml file -->\n<project>\n</project>\n')
            elif full_path.endswith('.yml') or full_path.endswith('.yaml'):
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        f.write('# auto-generated yml file\n')
            elif full_path.endswith('.gradle'):
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        f.write('// auto-generated gradle file\n')
            elif full_path.endswith('verify-setup.sh'):
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        f.write(get_verify_setup_sh())
                    os.chmod(full_path, 0o755)
            else:
                if not os.path.exists(full_path):
                    with open(full_path, 'w', encoding='utf-8') as f:
                        f.write('// auto-generated file\n')

if __name__ == '__main__':
    paths = parse_structure('spring-learning-repo-structure.md')
    build_repo('spring-mastery', paths)
    print("Done building repository structure.")
