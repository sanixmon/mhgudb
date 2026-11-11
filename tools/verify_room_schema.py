#!/usr/bin/env python3
"""Check that the bundled database matches the schema Room exported during the build.

Usage:
    python3 tools/verify_room_schema.py app/schemas/<db class>/<version>.json \
        app/src/main/assets/databases/mhgu_codex.db.gz

Room rejects a prepackaged database whose tables, columns, primary keys or indices
differ from its entities, and only at runtime on the device. This script runs in CI
so the mismatch fails the build instead.
"""

import gzip
import json
import sqlite3
import sys
import tempfile


def describe(db):
    shape = {}
    tables = [r[0] for r in db.execute(
        "SELECT name FROM sqlite_master WHERE type='table' "
        "AND name NOT LIKE 'sqlite_%' AND name NOT IN ('room_master_table', 'android_metadata')")]
    for table in tables:
        cols = sorted(
            (name, ctype.upper(), bool(notnull), pk)
            for _, name, ctype, notnull, _, pk in db.execute(f"PRAGMA table_info(`{table}`)"))
        indices = []
        for _, name, unique, origin, _ in db.execute(f"PRAGMA index_list(`{table}`)"):
            if origin != "c":
                continue
            index_cols = [r[2] for r in db.execute(f"PRAGMA index_info(`{name}`)")]
            indices.append((name, bool(unique), tuple(index_cols)))
        shape[table] = (cols, sorted(indices))
    return shape


def main():
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    schema = json.load(open(sys.argv[1]))["database"]

    expected = sqlite3.connect(":memory:")
    for entity in schema["entities"]:
        table = entity["tableName"]
        expected.execute(entity["createSql"].replace("${TABLE_NAME}", table))
        for index in entity.get("indices", []):
            expected.execute(index["createSql"].replace("${TABLE_NAME}", table))

    with tempfile.NamedTemporaryFile(suffix=".db") as tmp:
        with gzip.open(sys.argv[2], "rb") as f:
            tmp.write(f.read())
        tmp.flush()
        actual = sqlite3.connect(tmp.name)
        version = actual.execute("PRAGMA user_version").fetchone()[0]
        want, got = describe(expected), describe(actual)

    errors = []
    if version != schema["version"]:
        errors.append(f"user_version is {version}, Room expects {schema['version']}")
    for table in sorted(set(want) | set(got)):
        if table not in got:
            errors.append(f"missing table {table}")
        elif table not in want:
            errors.append(f"unexpected table {table}")
        elif want[table] != got[table]:
            errors.append(f"{table}:\n  room:  {want[table]}\n  asset: {got[table]}")
    if errors:
        print("Bundled database does not match the Room schema:")
        print("\n".join(errors))
        sys.exit(1)
    print(f"Bundled database matches Room schema v{version} ({len(want)} tables).")


if __name__ == "__main__":
    main()
