<?php
require_once __DIR__ . '/../function/DataBase.php';
$db = new DataBase();

header('Content-Type: application/json'); // Set response header to JSON

if (isset($_POST['first_name']) && isset($_POST['last_name']) && isset($_POST['email']) && isset($_POST['phone']) && isset($_POST['password'])) {
    if ($db->dbConnect()) {
        if ($db->signUp("users", $_POST['first_name'], $_POST['last_name'], $_POST['phone'], $_POST['email'], $_POST['password'])) {
            echo json_encode(["status" => "success", "message" => "Sign Up Success"]);
        } else {
            echo json_encode(["status" => "error", "message" => "Account Duplication Error"]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "Error: Database connection"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "All fields are required"]);
}
?>