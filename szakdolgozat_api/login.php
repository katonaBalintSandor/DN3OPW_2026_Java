<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$username = trim($_POST["username"] ?? "");
$password = trim($_POST["password"] ?? "");

if ($username === "" || $password === "") {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó felhasználónév vagy jelszó!"
    ]);
    exit;
}

$sql = "
    SELECT id, lastname, firstname, username, email, password
    FROM users
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

if ($user = $result->fetch_assoc()) {
    if (password_verify($password, $user["password"])) {
        echo json_encode([
            "success" => true,
            "user" => [
                "id" => (int)$user["id"],
                "lastname" => $user["lastname"],
                "firstname" => $user["firstname"],
                "username" => $user["username"],
                "email" => $user["email"]
            ]
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Hibás felhasználónév vagy jelszó!"
        ]);
    }
} else {
    echo json_encode([
        "success" => false,
        "message" => "Hibás felhasználónév vagy jelszó!"
    ]);
}

$stmt->close();
$conn->close();
?>