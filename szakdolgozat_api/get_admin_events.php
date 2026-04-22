<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$sql = "
    SELECT
        e.id,
        e.title,
        e.header,
        e.date,
        e.description,
        e.picture,
        e.admin_id,
        e.library_id,
        l.name AS library_name
    FROM events e
    INNER JOIN library l ON l.id = e.library_id
    ORDER BY e.date DESC, e.id DESC
";

$result = $conn->query($sql);

if (!$result) {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült betölteni az eseményeket.",
        "error" => $conn->error,
        "events" => []
    ]);
    exit;
}

$events = [];

while ($row = $result->fetch_assoc()) {
    $events[] = [
        "id" => (int)$row["id"],
        "title" => $row["title"],
        "header" => $row["header"],
        "date" => $row["date"],
        "description" => $row["description"],
        "picture" => $row["picture"],
        "admin_id" => (int)$row["admin_id"],
        "library_id" => (int)$row["library_id"],
        "library_name" => $row["library_name"]
    ];
}

echo json_encode([
    "success" => true,
    "events" => $events
]);

$conn->close();
?>