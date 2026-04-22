<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$book_id = intval($_POST["book_id"] ?? 0);
$library_id = intval($_POST["library_id"] ?? 0);

if ($book_id <= 0 || $library_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen könyv vagy könyvtár azonosító!"
    ]);
    exit;
}

$checkSql = "
    SELECT id
    FROM library_stock
    WHERE book_id = ? AND library_id = ?
    LIMIT 1
";
$checkStmt = $conn->prepare($checkSql);

if (!$checkStmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (check)!",
        "error" => $conn->error
    ]);
    exit;
}

$checkStmt->bind_param("ii", $book_id, $library_id);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows === 0) {
    $checkStmt->close();
    echo json_encode([
        "success" => false,
        "message" => "A könyv nem található ebben a könyvtárban!"
    ]);
    exit;
}
$checkStmt->close();

$sql = "
    SELECT 
        b.id,
        b.title,
        b.author,
        b.category,
        b.release_date,
        b.description,
        b.picture,
        ls.quantity,
        ls.library_id
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

if ($book = $result->fetch_assoc()) {
    echo json_encode([
        "success" => true,
        "message" => "A könyv betöltve.",
        "book" => [
            "id" => (int)$book["id"],
            "title" => $book["title"],
            "author" => $book["author"],
            "category" => $book["category"],
            "release_date" => $book["release_date"],
            "description" => $book["description"],
            "picture" => $book["picture"],
            "quantity" => (int)$book["quantity"],
            "library_id" => (int)$book["library_id"]
        ]
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "A könyv nem található!"
    ]);
}

$stmt->close();
$conn->close();
?>