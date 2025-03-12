<?php
require_once __DIR__ . '/../function/DataBase.php';

$db = new DataBase();

// Check if required fields are set
if (isset($_POST['user_id']) && isset($_POST['old_password']) && isset($_POST['new_password'])) {
    if ($db->dbConnect()) {
        // Retrieve and sanitize input data
        $user_id = $_POST['user_id'];
        $old_password = $_POST['old_password'];
        $new_password = $_POST['new_password'];

        // Validate the old password
        if ($db->validateOldPassword($user_id, $old_password)) {
            // Call the updatePassword method
            if ($db->updatePassword($user_id, $new_password)) {
                echo "Update Success";
            } else {
                echo "Update Failed";
            }
        } else {
            echo "Old password is incorrect";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>