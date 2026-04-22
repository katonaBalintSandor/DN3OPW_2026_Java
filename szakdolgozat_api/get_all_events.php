<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$sql = "
    SELECT 
        e.id,
        e.title,
        e.header,
        e.date,
        e.picture,
        e.description,
        e.admin_id,
        e.library_id,
        l.name AS library_name
    FROM events e
    INNER JOIN library l ON l.id = e.library_id
    INNER JOIN admins a ON a.id = e.admin_id
    ORDER BY e.date DESC, e.id DESC
";

$result = $conn->query($sql);

if (!$result) {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült lekérni az eseményeket.",
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
        "picture" => $row["picture"],
        "description" => $row["description"],
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