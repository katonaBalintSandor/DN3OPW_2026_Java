<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$sql = "
    SELECT 
        l.id,
        l.name,
        l.city,
        l.picture,
        COALESCE(SUM(ls.quantity), 0) AS total_books
    FROM library l
    LEFT JOIN library_stock ls ON l.id = ls.library_id
    GROUP BY l.id, l.name, l.city, l.picture
    ORDER BY l.name ASC
";

$result = $conn->query($sql);

if (!$result) {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült lekérni a könyvtárakat.",
        "error" => $conn->error,
        "libraries" => []
    ]);
    exit;
}

$libraries = [];

while ($row = $result->fetch_assoc()) {
    $libraries[] = [
        "id" => (int)$row["id"],
        "name" => $row["name"],
        "city" => $row["city"],
        "picture" => $row["picture"],
        "total_books" => (int)$row["total_books"]
    ];
}

echo json_encode([
    "success" => true,
    "libraries" => $libraries
]);

$conn->close();
?>