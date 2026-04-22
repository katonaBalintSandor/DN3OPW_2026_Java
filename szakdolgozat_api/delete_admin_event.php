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

$check = $conn->prepare("SELECT id FROM events WHERE id = ? LIMIT 1");
if (!$check) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$check->bind_param("i", $event_id);
$check->execute();
$result = $check->get_result();

if ($result->num_rows === 0) {
    $check->close();
    echo json_encode([
        "success" => false,
        "message" => "Az esemény nem található!"
    ]);
    exit;
}
$check->close();

$stmt = $conn->prepare("DELETE FROM events WHERE id = ?");
if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("i", $event_id);

if ($stmt->execute() && $stmt->affected_rows > 0) {
    echo json_encode([
        "success" => true,
        "message" => "Az esemény törölve."
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült törölni az eseményt.",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>