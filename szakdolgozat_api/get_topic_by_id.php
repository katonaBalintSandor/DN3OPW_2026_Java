<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$topic_id = isset($_GET["topic_id"]) ? intval($_GET["topic_id"]) : intval($_POST["topic_id"] ?? 0);

if ($topic_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen topic_id"
    ]);
    exit;
}

$sql = "
    SELECT
        t.id,
        t.user_id,
        t.book_id,
        t.topic,
        t.description,
        t.rating,
        u.username,
        u.firstname AS user_firstname,
        u.lastname AS user_lastname,
        b.title AS book_title,
        b.author AS book_author,
        b.picture AS book_picture
    FROM topics t
    INNER JOIN users u ON u.id = t.user_id
    INNER JOIN books b ON b.id = t.book_id
    WHERE t.id = ?
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

$stmt->bind_param("i", $topic_id);
$stmt->execute();
$result = $stmt->get_result();

$row = $result->fetch_assoc();

if (!$row) {
    $stmt->close();
    echo json_encode([
        "success" => false,
        "message" => "A téma nem található."
    ]);
    exit;
}

echo json_encode([
    "success" => true,
    "topic" => [
        "id" => (int)$row["id"],
        "user_id" => (int)$row["user_id"],
        "book_id" => (int)$row["book_id"],
        "topic" => $row["topic"],
        "description" => $row["description"],
        "rating" => (int)$row["rating"],
        "username" => $row["username"],
        "user_firstname" => $row["user_firstname"],
        "user_lastname" => $row["user_lastname"],
        "book_title" => $row["book_title"],
        "book_author" => $row["book_author"],
        "book_picture" => $row["book_picture"]
    ]
]);

$stmt->close();
$conn->close();
?>