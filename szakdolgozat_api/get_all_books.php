<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$sql = "
    SELECT DISTINCT
        b.id,
        b.title,
        b.author,
        b.picture
    FROM books b
    INNER JOIN library_stock ls ON ls.book_id = b.id
    ORDER BY b.title ASC
";

$result = $conn->query($sql);

if (!$result) {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült a könyveket lekérni.",
        "error" => $conn->error,
        "books" => []
    ]);
    exit;
}

$books = [];

while ($row = $result->fetch_assoc()) {
    $books[] = [
        "id" => (int)$row["id"],
        "title" => $row["title"],
        "author" => $row["author"],
        "picture" => $row["picture"]
    ];
}

echo json_encode([
    "success" => true,
    "books" => $books
]);

$conn->close();
?>