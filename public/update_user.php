<?php
require_once __DIR__ . '/../function/DataBase.php';

$db = new DataBase();

// Check if user_id is set
if (isset($_POST['user_id'])) {
    if ($db->dbConnect()) {
        // Prepare the update query
        $updates = [];
        if (isset($_POST['first_name'])) {
            $updates[] = "first_name = '" . $db->prepareData($_POST['first_name']) . "'";
        }
        if (isset($_POST['last_name'])) {
            $updates[] = "last_name = '" . $db->prepareData($_POST['last_name']) . "'";
        }
        if (isset($_POST['email'])) {
            $updates[] = "email = '" . $db->prepareData($_POST['email']) . "'";
        }
        if (isset($_POST['phone'])) {
            $updates[] = "phone = '" . $db->prepareData($_POST['phone']) . "'";
        }

        // If there are fields to update
        if (!empty($updates)) {
            $query = "UPDATE users SET " . implode(", ", $updates) . " WHERE user_id = '" . $db->prepareData($_POST['user_id']) . "'";
            if (mysqli_query($db->dbConnect(), $query)) {
                echo "Update Success";
            } else {
                echo "Update Failed: " . mysqli_error($db->dbConnect());
            }
        } else {
            echo "No fields to update";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "User ID is required";
}
?>