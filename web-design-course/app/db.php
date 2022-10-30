<?php
/*
 * Database settings
 */
define("DB_SERVER", "db");
define("DB_NAME", "web");
define("DB_USERNAME", "web");
define("DB_PASSWORD", "webdesigncourse");

try {
    $db = new PDO("mysql:host=".DB_SERVER.";dbname=".DB_NAME, DB_USERNAME, DB_PASSWORD);
}
catch (PDOException $e) {
    echo "Error!: " . $e->getMessage() . "<br/>";
    exit();
}
