<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$library_id = intval($_POST["library_id"] ?? 0);
$book_id = intval($_POST["book_id"] ?? 0);
$quantity = intval($_POST["quantity"] ?? -1);

if ($library_id <= 0 || $book_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Érvénytelen library_id vagy book_id"
    ]);
    exit;
}

if ($quantity < 0) {
    echo json_encode([
        "success" => false,
        "message" => "A mennyiség nem lehet negatív"
    ]);
    exit;
}

$checkSql = "
    SELECT library_id, book_id
    FROM library_stock
    WHERE library_id = ? AND book_id = ?
    LIMIT 1
";

$checkStmt = $conn->prepare($checkSql);
if (!$checkStmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$checkStmt->bind_param("ii", $library_id, $book_id);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows === 0) {
    $checkStmt->close();
    echo json_encode([
        "success" => false,
        "message" => "Nem található ilyen könyv-könyvtár készlet rekord!"
    ]);
    exit;
}
$checkStmt->close();

$sql = "
    UPDATE library_stock
    SET quantity = ?
    WHERE library_id = ? AND book_id = ?
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

$stmt->bind_param("iii", $quantity, $library_id, $book_id);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Mennyiség módosítva!"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült frissíteni!",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>