<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$library_id = 0;

if (isset($_GET["library_id"])) {
    $library_id = intval($_GET["library_id"]);
} elseif (isset($_POST["library_id"])) {
    $library_id = intval($_POST["library_id"]);
}

if ($library_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen vagy hiányzó library_id"
    ]);
    exit;
}

$checkLibrary = $conn->prepare("SELECT id FROM library WHERE id = ? LIMIT 1");
if (!$checkLibrary) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$checkLibrary->bind_param("i", $library_id);
$checkLibrary->execute();
$libraryResult = $checkLibrary->get_result();

if ($libraryResult->num_rows === 0) {
    $checkLibrary->close();
    echo json_encode([
        "success" => false,
        "message" => "A könyvtár nem található!"
    ]);
    exit;
}
$checkLibrary->close();

$sql = "SELECT
            b.id,
            b.title,
            b.author,
            b.category,
            b.picture,
            ls.quantity,
            ls.library_id
        FROM library_stock ls
        INNER JOIN books b ON b.id = ls.book_id
        WHERE ls.library_id = ?
        ORDER BY b.id DESC";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("i", $library_id);
$stmt->execute();
$result = $stmt->get_result();

$books = [];
while ($row = $result->fetch_assoc()) {
    $books[] = [
        "id" => (int)$row["id"],
        "title" => $row["title"],
        "author" => $row["author"],
        "category" => $row["category"],
        "picture" => $row["picture"],
        "quantity" => (int)$row["quantity"],
        "library_id" => (int)$row["library_id"]
    ];
}

echo json_encode([
    "success" => true,
    "message" => "Books loaded",
    "books" => $books
]);

$stmt->close();
$conn->close();
?>