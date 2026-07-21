from __future__ import annotations

import csv
import json
import sqlite3
from pathlib import Path
from typing import Any


# Thư mục gốc của project.
PROJECT_ROOT = Path(__file__).resolve().parents[1]

# Hai file CSV nguồn.
TOPICS_CSV = (
        PROJECT_ROOT
        / "data_source"
        / "topics.csv"
)

VOCABULARIES_CSV = (
        PROJECT_ROOT
        / "data_source"
        / "vocabularies.csv"
)

# Schema version 2 do Room tự sinh.
ROOM_SCHEMA_JSON = (
        PROJECT_ROOT
        / "app"
        / "schemas"
        / "com.example.EnglishWithStork.RoomDatabase.AppDatabase"
        / "2.json"
)

# Database hoàn chỉnh được tạo vào assets.
OUTPUT_DATABASE = (
        PROJECT_ROOT
        / "app"
        / "src"
        / "main"
        / "assets"
        / "database"
        / "english_with_stork.db"
)

TOPIC_HEADERS = [
    "id",
    "name",
    "image_name",
    "sort_order",
]

VOCABULARY_HEADERS = [
    "id",
    "topic_id",
    "english",
    "word_class",
    "vietnamese",
    "phonetic",
    "example_en",
    "example_vi",
    "image_name",
    "audio_name",
    "sort_order",
]


def require_file(path: Path) -> None:
    if not path.exists():
        raise FileNotFoundError(
            f"Không tìm thấy file:\n{path}"
        )


def read_csv_rows(
        path: Path,
        expected_headers: list[str],
) -> list[dict[str, str]]:

    require_file(path)

    with path.open(
            mode="r",
            encoding="utf-8-sig",
            newline="",
    ) as file:

        reader = csv.DictReader(file)

        actual_headers = reader.fieldnames or []

        if actual_headers != expected_headers:
            raise ValueError(
                f"\nFile có tên hoặc thứ tự cột không đúng:\n"
                f"{path}\n\n"
                f"Cột yêu cầu:\n{expected_headers}\n\n"
                f"Cột hiện tại:\n{actual_headers}\n"
            )

        rows: list[dict[str, str]] = []

        for row_number, raw_row in enumerate(
                reader,
                start=2,
        ):

            row = {
                key: (value or "").strip()
                for key, value in raw_row.items()
            }

            # Bỏ qua dòng hoàn toàn trống.
            if not any(row.values()):
                continue

            row["_row_number"] = str(row_number)

            rows.append(row)

        return rows


def required_text(
        row: dict[str, str],
        key: str,
        file_name: str,
) -> str:

    value = row.get(key, "").strip()

    if not value:
        raise ValueError(
            f"{file_name}, dòng {row['_row_number']}: "
            f"cột '{key}' đang trống."
        )

    return value


def required_int(
        row: dict[str, str],
        key: str,
        file_name: str,
) -> int:

    value = required_text(
        row,
        key,
        file_name,
    )

    try:
        return int(value)

    except ValueError as error:
        raise ValueError(
            f"{file_name}, dòng {row['_row_number']}: "
            f"cột '{key}' phải là số nguyên, "
            f"hiện tại là '{value}'."
        ) from error


def nullable_text(
        value: str,
) -> str | None:

    cleaned = value.strip()

    return cleaned if cleaned else None


def ensure_unique_ids(
        ids: list[int],
        file_name: str,
) -> None:

    seen: set[int] = set()
    duplicates: set[int] = set()

    for item_id in ids:

        if item_id in seen:
            duplicates.add(item_id)

        seen.add(item_id)

    if duplicates:

        duplicate_text = ", ".join(
            str(item_id)
            for item_id in sorted(duplicates)
        )

        raise ValueError(
            f"{file_name} có ID bị trùng: "
            f"{duplicate_text}"
        )


def load_topics() -> tuple[
    list[tuple[int, str, str, int]],
    set[int],
]:

    rows = read_csv_rows(
        TOPICS_CSV,
        TOPIC_HEADERS,
    )

    topics: list[
        tuple[int, str, str, int]
    ] = []

    topic_ids: list[int] = []

    for row in rows:

        topic_id = required_int(
            row,
            "id",
            "topics.csv",
        )

        name = required_text(
            row,
            "name",
            "topics.csv",
        )

        image_name = required_text(
            row,
            "image_name",
            "topics.csv",
        )

        sort_order = required_int(
            row,
            "sort_order",
            "topics.csv",
        )

        topics.append(
            (
                topic_id,
                name,
                image_name,
                sort_order,
            )
        )

        topic_ids.append(topic_id)

    ensure_unique_ids(
        topic_ids,
        "topics.csv",
    )

    return topics, set(topic_ids)


def load_vocabularies(
        valid_topic_ids: set[int],
) -> list[tuple[Any, ...]]:

    rows = read_csv_rows(
        VOCABULARIES_CSV,
        VOCABULARY_HEADERS,
    )

    vocabularies: list[tuple[Any, ...]] = []

    vocabulary_ids: list[int] = []

    for row in rows:

        vocabulary_id = required_int(
            row,
            "id",
            "vocabularies.csv",
        )

        topic_id = required_int(
            row,
            "topic_id",
            "vocabularies.csv",
        )

        if topic_id not in valid_topic_ids:
            raise ValueError(
                f"vocabularies.csv, dòng "
                f"{row['_row_number']}: "
                f"topic_id = {topic_id} "
                f"không tồn tại trong topics.csv."
            )

        english = required_text(
            row,
            "english",
            "vocabularies.csv",
        )

        vietnamese = required_text(
            row,
            "vietnamese",
            "vocabularies.csv",
        )

        sort_order = required_int(
            row,
            "sort_order",
            "vocabularies.csv",
        )

        vocabularies.append(
            (
                vocabulary_id,
                topic_id,
                english,
                nullable_text(row["word_class"]),
                vietnamese,
                nullable_text(row["phonetic"]),
                nullable_text(row["example_en"]),
                nullable_text(row["example_vi"]),
                nullable_text(row["image_name"]),
                nullable_text(row["audio_name"]),
                sort_order,
            )
        )

        vocabulary_ids.append(
            vocabulary_id
        )

    ensure_unique_ids(
        vocabulary_ids,
        "vocabularies.csv",
    )

    return vocabularies


def load_room_schema() -> dict[str, Any]:

    require_file(
        ROOM_SCHEMA_JSON
    )

    with ROOM_SCHEMA_JSON.open(
            mode="r",
            encoding="utf-8",
    ) as file:

        schema = json.load(file)

    database = schema.get("database")

    if not database:
        raise ValueError(
            "File 2.json không có phần database."
        )

    version = database.get("version")

    if version != 2:
        raise ValueError(
            f"Schema phải có version = 2, "
            f"nhưng hiện tại là {version}."
        )

    table_names = {
        entity.get("tableName")
        for entity in database.get(
            "entities",
            [],
        )
    }

    required_tables = {
        "table_users",
        "topics",
        "vocabularies",
    }

    missing_tables = (
            required_tables - table_names
    )

    if missing_tables:
        raise ValueError(
            "Schema Room đang thiếu bảng: "
            + ", ".join(
                sorted(missing_tables)
            )
        )

    return schema


def resolve_table_sql(
        sql: str,
        table_name: str,
) -> str:

    # Một số phiên bản Room dùng biến
    # ${TABLE_NAME} trong schema JSON.
    return sql.replace(
        "${TABLE_NAME}",
        table_name,
    )


def create_database() -> None:

    schema = load_room_schema()

    topics, valid_topic_ids = (
        load_topics()
    )

    vocabularies = load_vocabularies(
        valid_topic_ids
    )

    OUTPUT_DATABASE.parent.mkdir(
        parents=True,
        exist_ok=True,
    )

    if OUTPUT_DATABASE.exists():
        OUTPUT_DATABASE.unlink()

    connection = sqlite3.connect(
        OUTPUT_DATABASE
    )

    try:

        connection.execute(
            "PRAGMA journal_mode = DELETE"
        )

        connection.execute(
            "PRAGMA foreign_keys = ON"
        )

        database_schema = schema["database"]

        entities = database_schema.get(
            "entities",
            [],
        )

        connection.execute("BEGIN")

        # Tạo tất cả bảng đúng schema Room.
        for entity in entities:

            table_name = entity["tableName"]

            create_sql = resolve_table_sql(
                entity["createSql"],
                table_name,
            )

            connection.execute(create_sql)

        # Tạo index.
        for entity in entities:

            table_name = entity["tableName"]

            for index in entity.get(
                    "indices",
                    [],
            ):

                create_sql = index.get(
                    "createSql"
                )

                if create_sql:

                    connection.execute(
                        resolve_table_sql(
                            create_sql,
                            table_name,
                        )
                    )

        # Tạo view nếu có.
        for view in database_schema.get(
                "views",
                [],
        ):

            create_sql = view.get(
                "createSql"
            )

            if create_sql:
                connection.execute(
                    create_sql
                )

        # Tạo room_master_table và identity hash.
        for query in database_schema.get(
                "setupQueries",
                [],
        ):
            connection.execute(query)

        version = database_schema["version"]

        connection.execute(
            f"PRAGMA user_version = {version}"
        )

        # Nhập chủ đề.
        connection.executemany(
            """
            INSERT INTO topics (
                id,
                name,
                image_name,
                sort_order
            )
            VALUES (?, ?, ?, ?)
            """,
            topics,
        )

        # Nhập từ vựng.
        connection.executemany(
            """
            INSERT INTO vocabularies (
                id,
                topic_id,
                english,
                word_class,
                vietnamese,
                phonetic,
                example_en,
                example_vi,
                image_name,
                audio_name,
                sort_order
            )
            VALUES (
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            )
            """,
            vocabularies,
        )

        foreign_key_errors = (
            connection.execute(
                "PRAGMA foreign_key_check"
            ).fetchall()
        )

        if foreign_key_errors:

            raise ValueError(
                "Database có lỗi foreign key:\n"
                + "\n".join(
                    str(error)
                    for error in foreign_key_errors
                )
            )

        connection.commit()

        connection.execute("VACUUM")

        topic_count = (
            connection.execute(
                "SELECT COUNT(*) FROM topics"
            ).fetchone()[0]
        )

        vocabulary_count = (
            connection.execute(
                """
                SELECT COUNT(*)
                FROM vocabularies
                """
            ).fetchone()[0]
        )

        user_version = (
            connection.execute(
                "PRAGMA user_version"
            ).fetchone()[0]
        )

        print(
            "TẠO DATABASE THÀNH CÔNG"
        )

        print(
            f"Room version: {user_version}"
        )

        print(
            f"Số chủ đề: {topic_count}"
        )

        print(
            f"Số từ vựng: {vocabulary_count}"
        )

        print(
            f"Database:\n{OUTPUT_DATABASE}"
        )

    except Exception:

        connection.rollback()

        if OUTPUT_DATABASE.exists():
            OUTPUT_DATABASE.unlink()

        raise

    finally:

        connection.close()


if __name__ == "__main__":
    create_database()