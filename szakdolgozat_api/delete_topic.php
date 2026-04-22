<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$topic_id = intval($_POST["topic_id"] ?? 0);

if ($topic_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás topic_id"
    ]);
    exit;
}

$check = $conn->prepare("SELECT id FROM topics WHERE id = ? LIMIT 1");
if (!$check) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$check->bind_param("i", $topic_id);
$check->execute();
$result = $check->get_result();

if ($result->num_rows === 0) {
    $check->close();
    echo json_encode([
        "success" => false,
        "message" => "A téma nem található."
    ]);
    exit;
}
$check->close();

$conn->begin_transaction();

try {
    $stmt_comments = $conn->prepare("DELETE FROM comments WHERE topic_id = ?");
    if (!$stmt_comments) {
        throw new Exception("Prepare hiba (comments): " . $conn->error);
    }

    $stmt_comments->bind_param("i", $topic_id);
    if (!$stmt_comments->execute()) {
        throw new Exception("Nem sikerült törölni a hozzászólásokat: " . $stmt_comments->error);
    }
    $stmt_comments->close();

    $stmt_topic = $conn->prepare("DELETE FROM topics WHERE id = ?");
    if (!$stmt_topic) {
        throw new Exception("Prepare hiba (topics): " . $conn->error);
    }

    $stmt_topic->bind_param("i", $topic_id);
    if (!$stmt_topic->execute()) {
        throw new Exception("Nem sikerült törölni a témát: " . $stmt_topic->error);
    }

    if ($stmt_topic->affected_rows <= 0) {
        throw new Exception("A téma nem található vagy nem törölhető.");
    }
    $stmt_topic->close();

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "A téma és a hozzászólások törölve lettek."
    ]);
} catch (Exception $e) {
    $conn->rollback();

    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>