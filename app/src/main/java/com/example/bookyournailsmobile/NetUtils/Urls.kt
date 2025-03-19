package com.example.bookyournailsmobile.NetUtils

object Urls {
    const val ROOT = "http://"
    const val IP_ADDRESS = "192.168.68.106"
    const val DIRECTORY = "/BookYourNails/public/"
    const val URL_LOGIN = ROOT + IP_ADDRESS + DIRECTORY + "login.php"
    const val URL_REGISTER = ROOT + IP_ADDRESS + DIRECTORY + "signup.php"
    const val URL_CHANGE_ACCOUNT = ROOT + IP_ADDRESS + DIRECTORY + "update_user.php"
    const val URL_CHANGE_PASSWORD = ROOT + IP_ADDRESS + DIRECTORY + "updatePassword.php"
    const val URL_FORGOT_PASSWORD = ROOT + IP_ADDRESS + DIRECTORY + "send_otp.php"
}