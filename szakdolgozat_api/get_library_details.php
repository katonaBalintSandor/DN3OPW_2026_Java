<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$library_id = isset($_GET["library_id"]) ? intval($_GET["library_id"]) : intval($_POST["library_id"] ?? 0);

if ($library_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen library_id"
    ]);
    exit;
}

$librarySql = "
    SELECT id, name, city, picture
    FROM library
    WHERE id = ?
    LIMIT 1
";

$libraryStmt = $conn->prepare($librarySql);
if (!$libraryStmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (library)!",
        "error" => $conn->error
    ]);
    exit;
}

$libraryStmt->bind_param("i", $library_id);
$libraryStmt->execute();
$libraryResult = $libraryStmt->get_result();
$library = $libraryResult->fetch_assoc();

if (!$library) {
    $libraryStmt->close();
    echo json_encode([
        "success" => false,
        "message" => "A könyvtár nem található."
    ]);
    exit;
}

$booksSql = "
    SELECT 
        b.id,
        b.title,
        b.author,
        b.picture,
        b.category,
        ls.library_id,
        ls.quantity
    FROM library_stock ls
    INNER JOIN books b ON b.id = ls.book_id
    WHERE ls.library_id = ?
    ORDER BY b.title ASC
";

$booksStmt = $conn->prepare($booksSql);
if (!$booksStmt) {
    $libraryStmt->close();
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (books)!",
        "error" => $conn->error
    ]);
    exit;
}

$booksStmt->bind_param("i", $library_id);
$booksStmt->execute();
$booksResult = $booksStmt->get_result();

$books = [];

while ($row = $booksResult->fetch_assoc()) {
    $books[] = [
        "id" => (int)$row["id"],
        "title" => $row["title"],
        "author" => $row["author"],
        "picture" => $row["picture"],
        "category" => $row["category"],
        "library_id" => (int)$row["library_id"],
        "quantity" => (int)$row["quantity"]
    ];
}

echo json_encode([
    "success" => true,
    "library" => [
        "id" => (int)$library["id"],
        "name" => $library["name"],
        "city" => $library["city"],
        "picture" => $library["picture"]
    ],
    "books" => $books
]);

$libraryStmt->close();
$booksStmt->close();
$conn->close();
?>