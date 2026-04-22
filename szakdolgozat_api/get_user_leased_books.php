<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$user_id = isset($_GET["user_id"]) ? intval($_GET["user_id"]) : intval($_POST["user_id"] ?? 0);

if ($user_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen user_id",
        "books" => []
    ]);
    exit;
}

$checkUser = $conn->prepare("SELECT id FROM users WHERE id = ? LIMIT 1");
if (!$checkUser) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error,
        "books" => []
    ]);
    exit;
}

$checkUser->bind_param("i", $user_id);
$checkUser->execute();
$userResult = $checkUser->get_result();

if ($userResult->num_rows === 0) {
    $checkUser->close();
    echo json_encode([
        "success" => false,
        "message" => "A felhasználó nem található.",
        "books" => []
    ]);
    exit;
}
$checkUser->close();

$sql = "
    SELECT 
        ol.id AS lease_id,
        ol.book_id,
        ol.library_id,
        ol.user_id,
        b.title,
        b.author,
        l.name AS library_name,
        ol.leased_date,
        ol.returned_date
    FROM on_lease ol
    INNER JOIN books b ON b.id = ol.book_id
    INNER JOIN library l ON l.id = ol.library_id
    WHERE ol.user_id = ?
    ORDER BY ol.leased_date DESC, ol.id DESC
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "SQL prepare error",
        "error" => $conn->error,
        "books" => []
    ]);
    exit;
}

$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$books = [];

while ($row = $result->fetch_assoc()) {
    $books[] = [
        "lease_id" => (int)$row["lease_id"],
        "book_id" => (int)$row["book_id"],
        "library_id" => (int)$row["library_id"],
        "user_id" => (int)$row["user_id"],
        "title" => $row["title"],
        "author" => $row["author"],
        "library_name" => $row["library_name"],
        "leased_date" => $row["leased_date"],
        "returned_date" => $row["returned_date"]
    ];
}

echo json_encode([
    "success" => true,
    "books" => $books
]);

$stmt->close();
$conn->close();
?>