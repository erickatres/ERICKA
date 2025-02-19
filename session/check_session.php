<?php
session_start();
if (isset($_SESSION['user'])) {
    echo json_encode(["status" => "logged_in", "user" => $_SESSION['user']]);
} else {
    echo json_encode(["status" => "logged_out"]);
}
?>
