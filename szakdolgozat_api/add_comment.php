<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$topic_id = intval($_POST["topic_id"] ?? 0);
$user_id = intval($_POST["user_id"] ?? 0);
$comment = trim($_POST["comment"] ?? "");

if ($topic_id <= 0 || $user_id <= 0 || $comment === "") {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás adatok!"
    ]);
    exit;
}

$checkTopic = $conn->prepare("SELECT id FROM topics WHERE id = ? LIMIT 1");
$checkTopic->bind_param("i", $topic_id);
$checkTopic->execute();
$topicResult = $checkTopic->get_result();

if ($topicResult->num_rows === 0) {
    $checkTopic->close();
    echo json_encode([
        "success" => false,
        "message" => "A téma nem létezik!"
    ]);
    exit;
}
$checkTopic->close();

$checkUser = $conn->prepare("SELECT id FROM users WHERE id = ? LIMIT 1");
$checkUser->bind_param("i", $user_id);
$checkUser->execute();
$userResult = $checkUser->get_result();

if ($userResult->num_rows === 0) {
    $checkUser->close();
    echo json_encode([
        "success" => false,
        "message" => "A felhasználó nem létezik!"
    ]);
    exit;
}
$checkUser->close();

$sql = "INSERT INTO comments (topic_id, user_id, comment, created_at) VALUES (?, ?, ?, NOW())";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("iis", $topic_id, $user_id, $comment);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "A hozzászólás mentve."
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült menteni a hozzászólást.",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>