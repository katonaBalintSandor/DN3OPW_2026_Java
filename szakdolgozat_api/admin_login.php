<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$username = trim($_POST["username"] ?? "");
$password = trim($_POST["password"] ?? "");
$admin_code = trim($_POST["admin_code"] ?? "");

if ($username === "" || $password === "" || $admin_code === "") {
    echo json_encode([
        "success" => false,
        "message" => "Kérlek, töltsd ki az összes mezőt!"
    ]);
    exit;
}

$sql = "
    SELECT id, firstname, lastname, username, email, password, library_id, admin_code
    FROM admins
    WHERE username = ?
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

$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($admin = $result->fetch_assoc()) {
    $passwordOk = password_verify($password, $admin["password"]);
    $codeOk = trim($admin["admin_code"]) === $admin_code;

    if ($passwordOk && $codeOk) {
        echo json_encode([
            "success" => true,
            "message" => "Sikeres bejelentkezés",
            "admin_id" => (int)$admin["id"],
            "firstname" => $admin["firstname"],
            "lastname" => $admin["lastname"],
            "username" => $admin["username"],
            "email" => $admin["email"],
            "library_id" => (int)$admin["library_id"]
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Hibás bejelentkezési adatok!"
        ]);
    }
} else {
    echo json_encode([
        "success" => false,
        "message" => "Hibás bejelentkezési adatok!"
    ]);
}

$stmt->close();
$conn->close();
?>