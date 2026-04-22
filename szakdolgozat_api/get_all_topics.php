<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$sql = "
    SELECT 
        t.id,
        t.book_id,
        t.user_id,
        t.topic,
        t.description,
        t.rating,
        b.title AS book_title,
        b.picture AS book_picture,
        u.firstname AS user_firstname,
        u.lastname AS user_lastname
    FROM topics t
    INNER JOIN books b ON b.id = t.book_id
    INNER JOIN users u ON u.id = t.user_id
    ORDER BY t.id DESC
";

$result = $conn->query($sql);

if (!$result) {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült lekérni a témákat.",
        "error" => $conn->error,
        "topics" => []
    ]);
    exit;
}

$topics = [];

while ($row = $result->fetch_assoc()) {
    $topics[] = [
        "id" => (int)$row["id"],
        "book_id" => (int)$row["book_id"],
        "user_id" => (int)$row["user_id"],
        "topic" => $row["topic"],
        "description" => $row["description"],
        "rating" => (int)$row["rating"],
        "book_title" => $row["book_title"],
        "book_picture" => $row["book_picture"],
        "user_firstname" => $row["user_firstname"],
        "user_lastname" => $row["user_lastname"]
    ];
}

echo json_encode([
    "success" => true,
    "topics" => $topics
]);

$conn->close();
?>