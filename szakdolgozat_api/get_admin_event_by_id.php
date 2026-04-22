<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$event_id = intval($_POST["event_id"] ?? 0);

if ($event_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen esemény azonosító!"
    ]);
    exit;
}

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
    INNER JOIN admins a ON a.id = e.admin_id
    INNER JOIN library l ON l.id = e.library_id
    WHERE e.id = ?
    LIMIT 1
";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("i", $event_id);
$stmt->execute();
$result = $stmt->get_result();

if ($event = $result->fetch_assoc()) {
    echo json_encode([
        "success" => true,
        "event" => [
            "id" => (int)$event["id"],
            "title" => $event["title"],
            "header" => $event["header"],
            "date" => $event["date"],
            "description" => $event["description"],
            "picture" => $event["picture"],
            "admin_id" => (int)$event["admin_id"],
            "library_id" => (int)$event["library_id"],
            "library_name" => $event["library_name"]
        ]
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Az esemény nem található!"
    ]);
}

$stmt->close();
$conn->close();
?>