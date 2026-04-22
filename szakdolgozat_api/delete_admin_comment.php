<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$comment_id = intval($_POST["comment_id"] ?? 0);

if ($comment_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen hozzászólás azonosító!"
    ]);
    exit;
}

$check = $conn->prepare("SELECT id FROM comments WHERE id = ? LIMIT 1");
$check->bind_param("i", $comment_id);
$check->execute();
$result = $check->get_result();

if ($result->num_rows === 0) {
    $check->close();
    echo json_encode([
        "success" => false,
        "message" => "A hozzászólás nem létezik!"
    ]);
    exit;
}
$check->close();

$stmt = $conn->prepare("DELETE FROM comments WHERE id = ?");

if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("i", $comment_id);

if ($stmt->execute() && $stmt->affected_rows > 0) {
    echo json_encode([
        "success" => true,
        "message" => "A hozzászólás sikeresen törölve!"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült törölni a hozzászólást!",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>