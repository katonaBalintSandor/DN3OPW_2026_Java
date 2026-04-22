<?php
header("Content-Type: application/json; charset=UTF-8");

$host = "127.0.0.1";
$username = "root";
$password = "";

$dbname = "libraries";

if (isset($_GET["test"]) && $_GET["test"] === "1") {
    $dbname = "libraries_test";
}

$conn = new mysqli($host, $username, $password, $dbname);

if ($conn->connect_error) {
    echo json_encode([
        "success" => false,
        "message" => "Database connection failed: " . $conn->connect_error
    ]);
    exit;
}

$conn->set_charset("utf8mb4");
?>