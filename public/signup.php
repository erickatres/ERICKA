<?php
require_once __DIR__ . '/../function/DataBase.php';
$db = new DataBase();
if (isset($_POST['first_name']) && isset($_POST['last_name'])&& isset($_POST['email'])&&isset($_POST['password'])) {
    if ($db->dbConnect()) {
        if ($db->signUp("users", $_POST['first_name'], $_POST['last_name'], $_POST['email'], $_POST['password'])) {
            echo "Sign Up Success";
        } else echo "Sign up Failed";
    } else echo "Error: Database connection";
} else echo "All fields are required";
?>
