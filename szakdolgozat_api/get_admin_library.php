<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$library_id = 0;

if (isset($_GET["library_id"])) {
    $library_id = intval($_GET["library_id"]);
} elseif (isset($_POST["library_id"])) {
    $library_id = intval($_POST["library_id"]);
}

if ($library_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid or missing library_id"
    ]);
    exit;
}

$sql = "
    SELECT id, name, city, picture
    FROM library
    WHERE id = ?
    LIMIT 1
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

if ($library = $result->fetch_assoc()) {
    echo json_encode([
        "success" => true,
        "message" => "Library loaded",
        "library" => [
            "id" => (int)$library["id"],
            "name" => $library["name"],
            "city" => $library["city"],
            "picture" => $library["picture"]
        ]
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Library not found"
    ]);
}

$stmt->close();
$conn->close();
?>