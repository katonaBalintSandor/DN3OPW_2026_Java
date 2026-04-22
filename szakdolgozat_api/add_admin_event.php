<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$title = trim($_POST["title"] ?? "");
$header = trim($_POST["header"] ?? "");
$date = trim($_POST["date"] ?? "");
$description = trim($_POST["description"] ?? "");
$admin_id = intval($_POST["admin_id"] ?? 0);
$library_id = intval($_POST["library_id"] ?? 0);

if ($title === "" || $header === "" || $date === "" || $description === "" || $admin_id <= 0 || $library_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás adatok!"
    ]);
    exit;
}

$dateCheck = DateTime::createFromFormat("Y-m-d", $date);
if (!$dateCheck || $dateCheck->format("Y-m-d") !== $date) {
    echo json_encode([
        "success" => false,
        "message" => "Hibás dátumformátum! Használd ezt: ÉÉÉÉ-HH-NN"
    ]);
    exit;
}

$checkAdmin = $conn->prepare("SELECT id, library_id FROM admins WHERE id = ? LIMIT 1");
$checkAdmin->bind_param("i", $admin_id);
$checkAdmin->execute();
$adminResult = $checkAdmin->get_result();
$adminRow = $adminResult->fetch_assoc();

if (!$adminRow) {
    $checkAdmin->close();
    echo json_encode([
        "success" => false,
        "message" => "A megadott admin nem létezik!"
    ]);
    exit;
}
$checkAdmin->close();

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

if ((int)$adminRow["library_id"] !== $library_id) {
    echo json_encode([
        "success" => false,
        "message" => "Az admin csak a saját könyvtárához hozhat létre eseményt!"
    ]);
    exit;
}

$picture = null;
$path1 = null;
$path2 = null;

if (isset($_FILES["image"]) && $_FILES["image"]["error"] === UPLOAD_ERR_OK) {
    $allowedExtensions = ["jpg", "jpeg", "png", "webp"];
    $originalName = $_FILES["image"]["name"];
    $tmpName = $_FILES["image"]["tmp_name"];
    $ext = strtolower(pathinfo($originalName, PATHINFO_EXTENSION));

    if (!in_array($ext, $allowedExtensions, true)) {
        echo json_encode([
            "success" => false,
            "message" => "Nem támogatott képfájl!"
        ]);
        exit;
    }

    $picture = uniqid("event_", true) . "." . $ext;

    $dir1 = "D:/DN3OPW_2026_Python/app/assets/images/events/";
    $dir2 = "D:/xampp/htdocs/szakdolgozat_api/assets/images/events/";

    if (!is_dir($dir1) && !mkdir($dir1, 0777, true)) {
        echo json_encode([
            "success" => false,
            "message" => "Nem sikerült létrehozni a Python events mappát!"
        ]);
        exit;
    }

    if (!is_dir($dir2) && !mkdir($dir2, 0777, true)) {
        echo json_encode([
            "success" => false,
            "message" => "Nem sikerült létrehozni a XAMPP events mappát!"
        ]);
        exit;
    }

    $path1 = $dir1 . $picture;
    $path2 = $dir2 . $picture;

    if (!move_uploaded_file($tmpName, $path1)) {
        echo json_encode([
            "success" => false,
            "message" => "Nem sikerült elmenteni a képet!"
        ]);
        exit;
    }

    if (!copy($path1, $path2)) {
        @unlink($path1);
        echo json_encode([
            "success" => false,
            "message" => "Nem sikerült átmásolni a képet a XAMPP mappába!"
        ]);
        exit;
    }
}

$sql = "
    INSERT INTO events (title, header, date, description, picture, admin_id, library_id)
    VALUES (?, ?, ?, ?, ?, ?, ?)
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    if ($path1 && file_exists($path1)) @unlink($path1);
    if ($path2 && file_exists($path2)) @unlink($path2);

    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("sssssii", $title, $header, $date, $description, $picture, $admin_id, $library_id);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Az esemény sikeresen hozzáadva!"
    ]);
} else {
    if ($path1 && file_exists($path1)) @unlink($path1);
    if ($path2 && file_exists($path2)) @unlink($path2);

    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült hozzáadni az eseményt!",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>