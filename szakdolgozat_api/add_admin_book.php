<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$title = trim($_POST["title"] ?? "");
$author = trim($_POST["author"] ?? "");
$category = trim($_POST["category"] ?? "");
$release_date = trim($_POST["release_date"] ?? "");
$description = trim($_POST["description"] ?? "");
$uploaded_by = trim($_POST["uploaded_by"] ?? "");
$library_id = intval($_POST["library_id"] ?? 0);
$quantity = intval($_POST["quantity"] ?? 0);

if (
    $title === "" || $author === "" || $category === "" ||
    $release_date === "" || $description === "" ||
    $uploaded_by === "" || $library_id <= 0 || $quantity <= 0
) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás adatok!"
    ]);
    exit;
}

$dateCheck = DateTime::createFromFormat("Y-m-d", $release_date);
if (!$dateCheck || $dateCheck->format("Y-m-d") !== $release_date) {
    echo json_encode([
        "success" => false,
        "message" => "Hibás dátumformátum! Használd ezt: ÉÉÉÉ-HH-NN"
    ]);
    exit;
}

$checkLibrary = $conn->prepare("SELECT id FROM library WHERE id = ? LIMIT 1");
$checkLibrary->bind_param("i", $library_id);
$checkLibrary->execute();
$libraryResult = $checkLibrary->get_result();

if ($libraryResult->num_rows === 0) {
    $checkLibrary->close();
    echo json_encode([
        "success" => false,
        "message" => "A kiválasztott könyvtár nem létezik!"
    ]);
    exit;
}
$checkLibrary->close();

if (!isset($_FILES["image"]) || $_FILES["image"]["error"] !== UPLOAD_ERR_OK) {
    echo json_encode([
        "success" => false,
        "message" => "A kép feltöltése sikertelen!"
    ]);
    exit;
}

$allowedExtensions = ["jpg", "jpeg", "png", "webp"];
$originalName = $_FILES["image"]["name"];
$tmpName = $_FILES["image"]["tmp_name"];
$ext = strtolower(pathinfo($originalName, PATHINFO_EXTENSION));

if (!in_array($ext, $allowedExtensions, true)) {
    echo json_encode([
        "success" => false,
        "message" => "Nem támogatott képfájl! Csak: jpg, jpeg, png, webp"
    ]);
    exit;
}

$finalFileName = uniqid("book_", true) . "." . $ext;

$pythonDir = "D:/DN3OPW_2026_Python/app/assets/images/books/";
$xamppDir  = "D:/xampp/htdocs/szakdolgozat_api/assets/images/books/";

if (!is_dir($pythonDir) && !mkdir($pythonDir, 0777, true)) {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült létrehozni a Python képmappát!"
    ]);
    exit;
}

if (!is_dir($xamppDir) && !mkdir($xamppDir, 0777, true)) {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült létrehozni a XAMPP képmappát!"
    ]);
    exit;
}

$pythonPath = $pythonDir . $finalFileName;
$xamppPath = $xamppDir . $finalFileName;

if (!move_uploaded_file($tmpName, $xamppPath)) {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült elmenteni a képet a XAMPP books mappába!"
    ]);
    exit;
}

if (!copy($xamppPath, $pythonPath)) {
    @unlink($xamppPath);
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült átmásolni a képet a Python books mappába!"
    ]);
    exit;
}

$conn->begin_transaction();

try {
    $insertBookSql = "
        INSERT INTO books (title, author, category, release_date, description, picture, uploaded_by)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    ";
    $stmt1 = $conn->prepare($insertBookSql);
    if (!$stmt1) {
        throw new Exception("Könyv prepare hiba: " . $conn->error);
    }

    $stmt1->bind_param(
        "sssssss",
        $title,
        $author,
        $category,
        $release_date,
        $description,
        $finalFileName,
        $uploaded_by
    );

    if (!$stmt1->execute()) {
        throw new Exception("A könyv mentése sikertelen: " . $stmt1->error);
    }

    $book_id = $conn->insert_id;

    if ($book_id <= 0) {
        throw new Exception("A könyv mentése sikertelen!");
    }

    $insertStockSql = "
        INSERT INTO library_stock (library_id, book_id, quantity)
        VALUES (?, ?, ?)
    ";
    $stmt2 = $conn->prepare($insertStockSql);
    if (!$stmt2) {
        throw new Exception("Készlet prepare hiba: " . $conn->error);
    }

    $stmt2->bind_param("iii", $library_id, $book_id, $quantity);

    if (!$stmt2->execute()) {
        throw new Exception("A készlet mentése sikertelen: " . $stmt2->error);
    }

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "A könyv és a készlet sikeresen hozzáadva!",
        "book_id" => $book_id,
        "picture" => $finalFileName
    ]);

    $stmt1->close();
    $stmt2->close();

} catch (Exception $e) {
    $conn->rollback();

    if (file_exists($pythonPath)) {
        @unlink($pythonPath);
    }
    if (file_exists($xamppPath)) {
        @unlink($xamppPath);
    }

    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>