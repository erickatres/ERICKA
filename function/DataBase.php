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
function signUp($table, $first_name, $last_name, $email, $password)
{
    $this->dbConnect();
    $firstname = $this->prepareData($first_name);
    $lastname = $this->prepareData($last_name);
    $email = $this->prepareData($email);
    $password = password_hash($this->prepareData($password), PASSWORD_DEFAULT);
    $role = 'customer'; // Default role
    $this->sql = "INSERT INTO $table (first_name, last_name, password, email, role) VALUES ('$firstname', '$lastname', '$password', '$email', '$role')";
    return mysqli_query($this->connect, $this->sql);
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
