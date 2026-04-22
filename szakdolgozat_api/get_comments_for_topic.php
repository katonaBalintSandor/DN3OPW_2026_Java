<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$topic_id = isset($_GET["topic_id"]) ? intval($_GET["topic_id"]) : intval($_POST["topic_id"] ?? 0);

if ($topic_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy érvénytelen topic_id",
        "comments" => []
    ]);
    exit;
}

$checkTopic = $conn->prepare("SELECT id FROM topics WHERE id = ? LIMIT 1");
if (!$checkTopic) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error,
        "comments" => []
    ]);
    exit;
}

$checkTopic->bind_param("i", $topic_id);
$checkTopic->execute();
$topicResult = $checkTopic->get_result();

if ($topicResult->num_rows === 0) {
    $checkTopic->close();
    echo json_encode([
        "success" => false,
        "message" => "A téma nem található.",
        "comments" => []
    ]);
    exit;
}
$checkTopic->close();

$sql = "
    SELECT
        c.id,
        c.topic_id,
        c.user_id,
        u.username,
        u.firstname,
        u.lastname,
        c.created_at,
        c.comment
    FROM comments c
    INNER JOIN users u ON u.id = c.user_id
    WHERE c.topic_id = ?
    ORDER BY c.created_at DESC, c.id DESC
";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error,
        "comments" => []
    ]);
    exit;
}

$stmt->bind_param("i", $topic_id);
$stmt->execute();
$result = $stmt->get_result();

$comments = [];

while ($row = $result->fetch_assoc()) {
    $comments[] = [
        "id" => (int)$row["id"],
        "topic_id" => (int)$row["topic_id"],
        "user_id" => (int)$row["user_id"],
        "username" => $row["username"],
        "firstname" => $row["firstname"],
        "lastname" => $row["lastname"],
        "created_at" => $row["created_at"],
        "comment" => $row["comment"]
    ];
}

echo json_encode([
    "success" => true,
    "comments" => $comments
]);

$stmt->close();
$conn->close();
?>