<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$user_id = intval($_POST["user_id"] ?? 0);
$library_id = intval($_POST["library_id"] ?? 0);
$book_id = intval($_POST["book_id"] ?? 0);

if ($user_id <= 0 || $library_id <= 0 || $book_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás adatok!"
    ]);
    exit;
}

$conn->begin_transaction();

try {
    $checkUser = $conn->prepare("SELECT id FROM users WHERE id = ? LIMIT 1");
    if (!$checkUser) {
        throw new Exception("Prepare hiba (users): " . $conn->error);
    }
    $checkUser->bind_param("i", $user_id);
    $checkUser->execute();
    if ($checkUser->get_result()->num_rows === 0) {
        throw new Exception("A felhasználó nem létezik.");
    }
    $checkUser->close();

    $checkLibrary = $conn->prepare("SELECT id FROM library WHERE id = ? LIMIT 1");
    if (!$checkLibrary) {
        throw new Exception("Prepare hiba (library): " . $conn->error);
    }
    $checkLibrary->bind_param("i", $library_id);
    $checkLibrary->execute();
    if ($checkLibrary->get_result()->num_rows === 0) {
        throw new Exception("A könyvtár nem létezik.");
    }
    $checkLibrary->close();

    $checkBook = $conn->prepare("SELECT id FROM books WHERE id = ? LIMIT 1");
    if (!$checkBook) {
        throw new Exception("Prepare hiba (books): " . $conn->error);
    }
    $checkBook->bind_param("i", $book_id);
    $checkBook->execute();
    if ($checkBook->get_result()->num_rows === 0) {
        throw new Exception("A könyv nem létezik.");
    }
    $checkBook->close();

    $stockSql = "
        SELECT quantity
        FROM library_stock
        WHERE library_id = ? AND book_id = ?
        FOR UPDATE
    ";
    $stockStmt = $conn->prepare($stockSql);
    if (!$stockStmt) {
        throw new Exception("Prepare hiba (stock): " . $conn->error);
    }

    $stockStmt->bind_param("ii", $library_id, $book_id);
    $stockStmt->execute();
    $stockResult = $stockStmt->get_result();
    $stock = $stockResult->fetch_assoc();
    $stockStmt->close();

    if (!$stock) {
        throw new Exception("Ehhez a könyvhöz nincs készlet ebben a könyvtárban.");
    }

    if ((int)$stock["quantity"] <= 0) {
        throw new Exception("Nincs készleten.");
    }

    $updateSql = "
        UPDATE library_stock
        SET quantity = quantity - 1
        WHERE library_id = ? AND book_id = ? AND quantity > 0
    ";
    $updateStmt = $conn->prepare($updateSql);
    if (!$updateStmt) {
        throw new Exception("Prepare hiba (stock update): " . $conn->error);
    }

    $updateStmt->bind_param("ii", $library_id, $book_id);
    if (!$updateStmt->execute() || $updateStmt->affected_rows <= 0) {
        throw new Exception("Nem sikerült csökkenteni a készletet.");
    }
    $updateStmt->close();

    $leaseSql = "
        INSERT INTO on_lease (user_id, library_id, book_id, leased_date, returned_date)
        VALUES (?, ?, ?, NOW(), NULL)
    ";
    $leaseStmt = $conn->prepare($leaseSql);
    if (!$leaseStmt) {
        throw new Exception("Prepare hiba (lease insert): " . $conn->error);
    }

    $leaseStmt->bind_param("iii", $user_id, $library_id, $book_id);
    if (!$leaseStmt->execute()) {
        throw new Exception("Nem sikerült rögzíteni a kölcsönzést: " . $leaseStmt->error);
    }
    $leaseStmt->close();

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "Sikeres kölcsönzés"
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