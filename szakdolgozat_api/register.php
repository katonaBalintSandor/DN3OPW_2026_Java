<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$lastname  = trim($_POST["lastname"] ?? "");
$firstname = trim($_POST["firstname"] ?? "");
$username  = trim($_POST["username"] ?? "");
$email     = trim($_POST["email"] ?? "");
$password  = trim($_POST["password"] ?? "");

if ($lastname === "" || $firstname === "" || $username === "" || $email === "" || $password === "") {
    echo json_encode([
        "success" => false,
        "message" => "Kérlek, töltsd ki az összes mezőt!"
    ]);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "success" => false,
        "message" => "Hibás email cím!"
    ]);
    exit;
}

$checkSql = "SELECT id FROM users WHERE username = ? OR email = ? LIMIT 1";
$checkStmt = $conn->prepare($checkSql);

if (!$checkStmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba!",
        "error" => $conn->error
    ]);
    exit;
}

$checkStmt->bind_param("ss", $username, $email);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows > 0) {
    $checkStmt->close();
    echo json_encode([
        "success" => false,
        "message" => "Felhasználónév vagy email cím már regisztrálva van!"
    ]);
    exit;
}
$checkStmt->close();

$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

$sql = "
    INSERT INTO users (lastname, firstname, username, email, password)
    VALUES (?, ?, ?, ?, ?)
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

$stmt->bind_param("sssss", $lastname, $firstname, $username, $email, $hashedPassword);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Sikeres regisztráció"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Sikertelen regisztráció",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>