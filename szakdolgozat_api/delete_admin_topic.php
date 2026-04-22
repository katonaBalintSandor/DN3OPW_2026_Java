<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$topic_id = intval($_POST["topic_id"] ?? 0);

if ($topic_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen téma azonosító!"
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
        "message" => "A téma nem található!"
    ]);
    exit;
}
$check->close();

$conn->begin_transaction();

try {
    $stmt1 = $conn->prepare("DELETE FROM comments WHERE topic_id = ?");
    if (!$stmt1) {
        throw new Exception("Prepare hiba (comments): " . $conn->error);
    }

    $stmt1->bind_param("i", $topic_id);
    if (!$stmt1->execute()) {
        throw new Exception("Nem sikerült törölni a hozzászólásokat: " . $stmt1->error);
    }
    $stmt1->close();

    $stmt2 = $conn->prepare("DELETE FROM topics WHERE id = ?");
    if (!$stmt2) {
        throw new Exception("Prepare hiba (topics): " . $conn->error);
    }

    $stmt2->bind_param("i", $topic_id);
    if (!$stmt2->execute()) {
        throw new Exception("Nem sikerült törölni a témát: " . $stmt2->error);
    }

    if ($stmt2->affected_rows <= 0) {
        throw new Exception("A téma törlése sikertelen!");
    }
    $stmt2->close();

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "A téma sikeresen törölve!"
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