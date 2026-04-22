<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$library_id = intval($_POST["library_id"] ?? 0);

if ($library_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás library_id"
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

$sql = "
    SELECT 
        ol.id AS lease_id,
        ol.book_id,
        ol.user_id,
        ol.library_id,
        ol.leased_date,
        ol.returned_date,
        b.title,
        b.author,
        b.category,
        b.picture,
        u.username
    FROM on_lease ol
    INNER JOIN books b ON b.id = ol.book_id
    INNER JOIN users u ON u.id = ol.user_id
    WHERE ol.library_id = ?
      AND ol.returned_date IS NULL
    ORDER BY ol.leased_date DESC
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

$stmt->bind_param("i", $library_id);
$stmt->execute();
$result = $stmt->get_result();

$leases = [];

while ($row = $result->fetch_assoc()) {
    $leases[] = [
        "lease_id" => (int)$row["lease_id"],
        "book_id" => (int)$row["book_id"],
        "user_id" => (int)$row["user_id"],
        "library_id" => (int)$row["library_id"],
        "title" => $row["title"],
        "author" => $row["author"],
        "category" => $row["category"],
        "picture" => $row["picture"],
        "username" => $row["username"],
        "leased_date" => $row["leased_date"],
        "returned_date" => $row["returned_date"]
    ];
}

echo json_encode([
    "success" => true,
    "leases" => $leases
]);

$stmt->close();
$conn->close();
?>