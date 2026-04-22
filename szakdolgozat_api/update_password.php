<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$user_id = intval($_POST["user_id"] ?? 0);
$password = trim($_POST["password"] ?? "");

if ($user_id <= 0 || $password === "") {
    echo json_encode([
        "success" => false,
        "message" => "Kérlek, töltsd ki az összes mezőt!"
    ]);
    exit;
}

$checkUser = $conn->prepare("SELECT id FROM users WHERE id = ? LIMIT 1");
if (!$checkUser) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}
$checkUser->bind_param("i", $user_id);
$checkUser->execute();

if ($checkUser->get_result()->num_rows === 0) {
    $checkUser->close();
    echo json_encode([
        "success" => false,
        "message" => "A felhasználó nem található!"
    ]);
    exit;
}
$checkUser->close();

$hashed_password = password_hash($password, PASSWORD_DEFAULT);

$sql = "UPDATE users SET password = ? WHERE id = ?";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "SQL hiba történt!",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("si", $hashed_password, $user_id);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "A jelszó sikeresen módosítva!"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült módosítani a jelszót!",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>