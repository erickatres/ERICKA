<?php
require_once __DIR__ . '/../function/DataBase.php';
$db = new DataBase();

if ($db->dbConnect()) {
    $data  = $db->login_test("users",$_POST['email'], $_POST['password']);       
    if ($data) {
        echo json_encode($data);
    } else {
        echo "null";
    }
} else {
    echo "Error: Database connection";
}
?>
