<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

$topic = trim($_POST["topic"] ?? "");
$description = trim($_POST["description"] ?? "");
$rating = intval($_POST["rating"] ?? 0);
$book_id = intval($_POST["book_id"] ?? 0);
$user_id = intval($_POST["user_id"] ?? 0);

if ($topic === "" || $description === "" || $rating < 1 || $rating > 5 || $book_id <= 0 || $user_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Minden mező kitöltése kötelező!"
    ]);
    exit;
}

$checkBook = $conn->prepare("SELECT id FROM books WHERE id = ? LIMIT 1");
if (!$checkBook) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (books).",
        "error" => $conn->error
    ]);
    exit;
}
$checkBook->bind_param("i", $book_id);
$checkBook->execute();
$bookResult = $checkBook->get_result();

if ($bookResult->num_rows === 0) {
    $checkBook->close();
    echo json_encode([
        "success" => false,
        "message" => "A kiválasztott könyv nem létezik."
    ]);
    exit;
}
$checkBook->close();

$checkUser = $conn->prepare("SELECT id FROM users WHERE id = ? LIMIT 1");
if (!$checkUser) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (users).",
        "error" => $conn->error
    ]);
    exit;
}
$checkUser->bind_param("i", $user_id);
$checkUser->execute();
$userResult = $checkUser->get_result();

if ($userResult->num_rows === 0) {
    $checkUser->close();
    echo json_encode([
        "success" => false,
        "message" => "A felhasználó nem létezik."
    ]);
    exit;
}
$checkUser->close();

$stmt = $conn->prepare("
    INSERT INTO topics (topic, description, rating, book_id, user_id)
    VALUES (?, ?, ?, ?, ?)
");

if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (insert).",
        "error" => $conn->error
    ]);
    $conn->close();
    exit;
}

$stmt->bind_param("ssiii", $topic, $description, $rating, $book_id, $user_id);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "A téma sikeresen létrehozva!",
        "topic_id" => $conn->insert_id
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Nem sikerült a téma létrehozása.",
        "error" => $stmt->error
    ]);
}

$stmt->close();
$conn->close();
?>