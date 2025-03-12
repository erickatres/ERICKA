<?php
require_once __DIR__ . '/../config/DataBaseConfig.php';

class DataBase
{
    private $connect;
    private $sql;
    protected $servername;
    protected $username;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $this->connect = null;
        $this->sql = null;
        $dbc = new DataBaseConfig();
        $this->servername = $dbc->servername;
        $this->username = $dbc->username;
        $this->password = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    function dbConnect()
    {
        $this->connect = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        return $this->connect;
    }

    function prepareData($data)
    {
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }

    function logIn($table, $firstname, $password)
    {
        $this->dbConnect();
        $username = $this->prepareData($firstname);
        $password = $this->prepareData($password);
        $this->sql = "SELECT * FROM $table WHERE firstname = '$firstname'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            if ($row['firstname'] == $username && password_verify($password, $row['password'])) {
                return $row;
            }
        }
        return null;
    }

    function storeSessionToken($userId, $sessionToken) {
        $this->dbConnect();
        $userId = $this->prepareData($userId);
        $sessionToken = $this->prepareData($sessionToken);
        $this->sql = "UPDATE users SET session_token = '$sessionToken' WHERE id = '$userId'";
        return mysqli_query($this->connect, $this->sql);
    }

    function login_test($table, $email, $password)
    {
        $email = $this->prepareData($email);
        $password = $this->prepareData($password);
        $this->sql = "select * from " . $table . " where email = '" . $email . "'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $email = $row['email'];
            $dbpassword = $row['password']; 
            if ($email == $email && password_verify($password, $dbpassword)) {
                return $row; // Ensure this includes the 'id' field
            } else return null;
        } else {
            return null;
        }
    }

    function signUp($table, $first_name, $last_name, $phone, $email, $password)
    {
        $this->dbConnect();
        $firstname = $this->prepareData($first_name);
        $lastname = $this->prepareData($last_name);
        $email = $this->prepareData($email);
        $phone = $this->prepareData($phone);
        $password = password_hash($this->prepareData($password), PASSWORD_DEFAULT);
        $role = 'customer'; // Default role
    
        // Check if email already exists
        $checkEmailQuery = "SELECT * FROM $table WHERE email = '$email'";
        $checkEmailResult = mysqli_query($this->connect, $checkEmailQuery);
    
        if (mysqli_num_rows($checkEmailResult) > 0) {
            return false; // Email already exists
        }
    
        $this->sql = "INSERT INTO $table (first_name, last_name, password, phone, email, role) VALUES ('$firstname', '$lastname', '$password', '$phone', '$email', '$role')";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            error_log("SQL Error: " . mysqli_error($this->connect)); // Log SQL errors
            return false;
        }
    }

    function signUp2($table, $username)
    {
        $this->dbConnect();
        $username = $this->prepareData($username);
        $this->sql = "INSERT INTO $table (username) VALUES ('$username')";
        return mysqli_query($this->connect, $this->sql);
    }

    function booking($table, $date, $user_id)
    {
        $this->dbConnect();
        $date = $this->prepareData($date);
        $user_id = $this->prepareData($user_id);
        $this->sql = "INSERT INTO $table (date, user_id) VALUES ('$date', '$user_id')";
        return mysqli_query($this->connect, $this->sql);
    }

    function updateUser($table, $user_id, $first_name = null, $last_name = null, $email = null, $mobile_number = null) {
        $this->dbConnect();
        $user_id = $this->prepareData($user_id);
    
        // Prepare the update query
        $updates = [];
        if ($first_name !== null) {
            $updates[] = "first_name = '" . $this->prepareData($first_name) . "'";
        }
        if ($last_name !== null) {
            $updates[] = "last_name = '" . $this->prepareData($last_name) . "'";
        }
        if ($email !== null) {
            $updates[] = "email = '" . $this->prepareData($email) . "'";
        }
        if ($mobile_number !== null) {
            $updates[] = "phone = '" . $this->prepareData($mobile_number) . "'";
        }
    
        // If there are fields to update
        if (!empty($updates)) {
            $this->sql = "UPDATE $table SET " . implode(", ", $updates) . " WHERE user_id = '$user_id'";
            return mysqli_query($this->connect, $this->sql);
        } else {
            return false; // No fields to update
        }
    }

    // Add the updatePassword function
    function updatePassword($user_id, $new_password) {
        $this->dbConnect();
        $user_id = $this->prepareData($user_id);
        $new_password = $this->prepareData($new_password);

        // Hash the new password
        $hashed_new_password = password_hash($new_password, PASSWORD_DEFAULT);

        // Prepare the SQL query
        $this->sql = "UPDATE users SET password = '$hashed_new_password' WHERE user_id = '$user_id'";

        // Execute the query
        if (mysqli_query($this->connect, $this->sql)) {
            return true; // Update successful
        } else {
            return false; // Update failed
        }
    }
    function validateOldPassword($user_id, $old_password) {
        $this->dbConnect();
        $user_id = $this->prepareData($user_id);
        $old_password = $this->prepareData($old_password);
    
        // Fetch the current password from the database
        $this->sql = "SELECT password FROM users WHERE user_id = '$user_id'";
        $result = mysqli_query($this->connect, $this->sql);
    
        if ($result && mysqli_num_rows($result) > 0) {
            $row = mysqli_fetch_assoc($result);
            $db_password = $row['password'];
    
            // Verify the old password
            if (password_verify($old_password, $db_password)) {
                return true;
            }
        }
        return false;
    }

    public function getBanners()
    {
        $connection = $this->connectToDatabase();
        if (!$connection) {
            return;
        }
        $query = "SELECT * FROM banner";
        $result = mysqli_query($connection, $query);
        if (!$result) {
            echo json_encode(["error" => "Query failed: " . mysqli_error($connection)]);
            mysqli_close($connection);
            return;
        }
        $banners = mysqli_fetch_all($result, MYSQLI_ASSOC);
        echo json_encode(empty($banners) ? ["message" => "No data found in the banner table"] : $banners);
        mysqli_free_result($result);
        mysqli_close($connection);
    }

    private function connectToDatabase()
    {
        $connection = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        if (!$connection) {
            echo json_encode(["error" => "Connection failed: " . mysqli_connect_error()]);
        }
        return $connection;
    }
}
?>