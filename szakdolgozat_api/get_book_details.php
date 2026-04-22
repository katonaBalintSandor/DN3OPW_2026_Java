<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$book_id = isset($_GET["book_id"]) ? intval($_GET["book_id"]) : intval($_POST["book_id"] ?? 0);
$library_id = isset($_GET["library_id"]) ? intval($_GET["library_id"]) : intval($_POST["library_id"] ?? 0);

if ($book_id <= 0 || $library_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen book_id vagy library_id"
    ]);
    exit;
}

$sql = "
    SELECT 
        b.id,
        b.title,
        b.author,
        b.picture,
        b.category,
        b.release_date,
        b.description,
        ls.library_id,
        ls.quantity
    FROM books b
    INNER JOIN library_stock ls ON ls.book_id = b.id
    WHERE b.id = ? AND ls.library_id = ?
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

$stmt->bind_param("ii", $book_id, $library_id);
$stmt->execute();
$result = $stmt->get_result();

$book = $result->fetch_assoc();

if (!$book) {
    $stmt->close();
    echo json_encode([
        "success" => false,
        "message" => "A könyv nem található ebben a könyvtárban."
    ]);
    exit;
}

echo json_encode([
    "success" => true,
    "book" => [
        "id" => (int)$book["id"],
        "title" => $book["title"],
        "author" => $book["author"],
        "picture" => $book["picture"],
        "category" => $book["category"],
        "release_date" => $book["release_date"],
        "description" => $book["description"],
        "library_id" => (int)$book["library_id"],
        "quantity" => (int)$book["quantity"]
    ]
]);

$stmt->close();
$conn->close();
?>