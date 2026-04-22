<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$comment_id = intval($_POST["comment_id"] ?? 0);
$user_id = intval($_POST["user_id"] ?? 0);

if ($comment_id <= 0 || $user_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás adatok!"
    ]);
    exit;
}

$check = $conn->prepare("SELECT id, user_id FROM comments WHERE id = ? LIMIT 1");
if (!$check) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$check->bind_param("i", $comment_id);
$check->execute();
$result = $check->get_result();
$row = $result->fetch_assoc();
$check->close();

if (!$row) {
    echo json_encode([
        "success" => false,
        "message" => "A hozzászólás nem található."
    ]);
    exit;
}

if ((int)$row["user_id"] !== $user_id) {
    echo json_encode([
        "success" => false,
        "message" => "Nincs jogosultságod törölni ezt a hozzászólást."
    ]);
    exit;
}

$sql = "DELETE FROM comments WHERE id = ? AND user_id = ?";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("ii", $comment_id, $user_id);

if ($stmt->execute() && $stmt->affected_rows > 0) {
    echo json_encode([
        "success" => true,
        "message" => "A hozzászólás törölve."
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült törölni a hozzászólást.",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>