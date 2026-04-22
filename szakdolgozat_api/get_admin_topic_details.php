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

$topicSql = "
    SELECT
        t.id,
        t.book_id,
        t.user_id,
        t.topic,
        t.description,
        t.rating,
        b.title AS book_title,
        b.author AS book_author,
        b.picture AS book_picture,
        u.firstname AS user_firstname,
        u.lastname AS user_lastname
    FROM topics t
    INNER JOIN books b ON b.id = t.book_id
    INNER JOIN users u ON u.id = t.user_id
    WHERE t.id = ?
    LIMIT 1
";

$stmt = $conn->prepare($topicSql);
if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (topic)!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("i", $topic_id);
$stmt->execute();
$result = $stmt->get_result();
$topic = $result->fetch_assoc();

if (!$topic) {
    $stmt->close();
    echo json_encode([
        "success" => false,
        "message" => "A téma nem található!"
    ]);
    exit;
}

$commentsSql = "
    SELECT
        c.id,
        c.topic_id,
        c.user_id,
        c.comment,
        c.created_at,
        u.firstname,
        u.lastname,
        u.username
    FROM comments c
    INNER JOIN users u ON u.id = c.user_id
    WHERE c.topic_id = ?
    ORDER BY c.id DESC
";

$stmt2 = $conn->prepare($commentsSql);
if (!$stmt2) {
    $stmt->close();
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (comments)!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt2->bind_param("i", $topic_id);
$stmt2->execute();
$result2 = $stmt2->get_result();

$comments = [];
while ($row = $result2->fetch_assoc()) {
    $comments[] = [
        "id" => (int)$row["id"],
        "topic_id" => (int)$row["topic_id"],
        "user_id" => (int)$row["user_id"],
        "username" => $row["username"],
        "firstname" => $row["firstname"],
        "lastname" => $row["lastname"],
        "comment" => $row["comment"],
        "created_at" => $row["created_at"]
    ];
}

echo json_encode([
    "success" => true,
    "topic" => [
        "id" => (int)$topic["id"],
        "book_id" => (int)$topic["book_id"],
        "user_id" => (int)$topic["user_id"],
        "topic" => $topic["topic"],
        "description" => $topic["description"],
        "rating" => (int)$topic["rating"],
        "book_title" => $topic["book_title"],
        "book_author" => $topic["book_author"],
        "book_picture" => $topic["book_picture"],
        "user_firstname" => $topic["user_firstname"],
        "user_lastname" => $topic["user_lastname"]
    ],
    "comments" => $comments
]);

$stmt->close();
$stmt2->close();
$conn->close();
?>