<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$lease_id = intval($_POST["lease_id"] ?? 0);
$book_id = intval($_POST["book_id"] ?? 0);
$library_id = intval($_POST["library_id"] ?? 0);

if ($lease_id <= 0 || $book_id <= 0 || $library_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás adatok!"
    ]);
    exit;
}

$conn->begin_transaction();

try {
    $checkSql = "
        SELECT id, book_id, library_id, returned_date
        FROM on_lease
        WHERE id = ?
        FOR UPDATE
    ";
    $checkStmt = $conn->prepare($checkSql);
    if (!$checkStmt) {
        throw new Exception("Prepare hiba (lease check): " . $conn->error);
    }

    $checkStmt->bind_param("i", $lease_id);
    $checkStmt->execute();
    $lease = $checkStmt->get_result()->fetch_assoc();
    $checkStmt->close();

    if (!$lease) {
        throw new Exception("A kölcsönzés nem található.");
    }

    if ((int)$lease["book_id"] !== $book_id || (int)$lease["library_id"] !== $library_id) {
        throw new Exception("A kölcsönzés adatai nem egyeznek.");
    }

    if ($lease["returned_date"] !== null) {
        throw new Exception("A könyv már vissza lett véve.");
    }

    $returnSql = "UPDATE on_lease SET returned_date = NOW() WHERE id = ?";
    $returnStmt = $conn->prepare($returnSql);
    if (!$returnStmt) {
        throw new Exception("Prepare hiba (return update): " . $conn->error);
    }

    $returnStmt->bind_param("i", $lease_id);
    if (!$returnStmt->execute() || $returnStmt->affected_rows <= 0) {
        throw new Exception("Nem sikerült lezárni a kölcsönzést.");
    }
    $returnStmt->close();

    $stockSql = "
        UPDATE library_stock
        SET quantity = quantity + 1
        WHERE library_id = ? AND book_id = ?
    ";
    $stockStmt = $conn->prepare($stockSql);
    if (!$stockStmt) {
        throw new Exception("Prepare hiba (stock update): " . $conn->error);
    }

    $stockStmt->bind_param("ii", $library_id, $book_id);
    if (!$stockStmt->execute() || $stockStmt->affected_rows <= 0) {
        throw new Exception("Nem sikerült frissíteni a készletet.");
    }
    $stockStmt->close();

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "A könyv sikeresen visszavéve!"
    ]);

} catch (Exception $e) {
    $conn->rollback();

    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>