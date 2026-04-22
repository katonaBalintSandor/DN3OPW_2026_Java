<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$event_id = intval($_POST["event_id"] ?? 0);
$title = trim($_POST["title"] ?? "");
$header = trim($_POST["header"] ?? "");
$date = trim($_POST["date"] ?? "");
$description = trim($_POST["description"] ?? "");
$old_picture = trim($_POST["old_picture"] ?? "");

if ($event_id <= 0 || $title === "" || $header === "" || $date === "" || $description === "") {
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

$checkEvent = $conn->prepare("SELECT id FROM events WHERE id = ? LIMIT 1");
if (!$checkEvent) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}
$checkEvent->bind_param("i", $event_id);
$checkEvent->execute();
if ($checkEvent->get_result()->num_rows === 0) {
    $checkEvent->close();
    echo json_encode([
        "success" => false,
        "message" => "Az esemény nem található!"
    ]);
    exit;
}
$checkEvent->close();

$finalPicture = $old_picture;

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

    $finalPicture = uniqid("event_", true) . "." . $ext;

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

    $path1 = $dir1 . $finalPicture;
    $path2 = $dir2 . $finalPicture;

    if (!move_uploaded_file($tmpName, $path1)) {
        echo json_encode([
            "success" => false,
            "message" => "Nem sikerült elmenteni az új képet!"
        ]);
        exit;
    }

    if (!copy($path1, $path2)) {
        @unlink($path1);
        echo json_encode([
            "success" => false,
            "message" => "Nem sikerült átmásolni az új képet a XAMPP mappába!"
        ]);
        exit;
    }
}

$sql = "
    UPDATE events
    SET title = ?, header = ?, date = ?, description = ?, picture = ?
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

$stmt->bind_param("sssssi", $title, $header, $date, $description, $finalPicture, $event_id);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Az esemény sikeresen módosítva!"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült módosítani az eseményt!",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>