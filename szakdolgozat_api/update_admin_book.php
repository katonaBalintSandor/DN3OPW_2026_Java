<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$book_id = intval($_POST["book_id"] ?? 0);
$title = trim($_POST["title"] ?? "");
$author = trim($_POST["author"] ?? "");
$category = trim($_POST["category"] ?? "");
$release_date = trim($_POST["release_date"] ?? "");
$description = trim($_POST["description"] ?? "");
$old_picture = trim($_POST["old_picture"] ?? "");

if ($book_id <= 0 || $title === "" || $author === "" || $category === "" || $release_date === "" || $description === "") {
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

$checkBook = $conn->prepare("SELECT id FROM books WHERE id = ? LIMIT 1");
if (!$checkBook) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}
$checkBook->bind_param("i", $book_id);
$checkBook->execute();
if ($checkBook->get_result()->num_rows === 0) {
    $checkBook->close();
    echo json_encode([
        "success" => false,
        "message" => "A könyv nem található!"
    ]);
    exit;
}
$checkBook->close();

$finalPicture = $old_picture;

if (isset($_FILES["image"]) && $_FILES["image"]["error"] === UPLOAD_ERR_OK) {
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

    $finalPicture = uniqid("book_", true) . "." . $ext;

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

    $xamppPath = $xamppDir . $finalPicture;
    $pythonPath = $pythonDir . $finalPicture;

    if (!move_uploaded_file($tmpName, $xamppPath)) {
        echo json_encode([
            "success" => false,
            "message" => "Nem sikerült elmenteni az új képet a XAMPP mappába!"
        ]);
        exit;
    }

    if (!copy($xamppPath, $pythonPath)) {
        @unlink($xamppPath);
        echo json_encode([
            "success" => false,
            "message" => "Nem sikerült átmásolni az új képet a Python mappába!"
        ]);
        exit;
    }
}

$sql = "
    UPDATE books
    SET title = ?, author = ?, category = ?, release_date = ?, description = ?, picture = ?
    WHERE id = ?
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

$stmt->bind_param(
    "ssssssi",
    $title,
    $author,
    $category,
    $release_date,
    $description,
    $finalPicture,
    $book_id
);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "A könyv sikeresen módosítva!"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült módosítani a könyvet!",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>