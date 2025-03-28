package com.example.bookyournailsmobile.NetUtils

object Urls {
    const val ROOT = "http://192.168.68.109:8000/"
    const val URL_LOGIN = ROOT + "login2"
   // const val ROOT = "http://"
    const val IP_ADDRESS = "172.20.10.3:8000"
    const val DIRECTORY = "/"
    //const val URL_LOGIN = ROOT + IP_ADDRESS + DIRECTORY + "login"
    const val URL_REGISTER = ROOT + IP_ADDRESS + DIRECTORY + "signup"
    const val URL_CHANGE_ACCOUNT = ROOT + IP_ADDRESS + DIRECTORY + "update_user.php"
    const val URL_CHANGE_PASSWORD = ROOT + IP_ADDRESS + DIRECTORY + "updatePassword.php"
    const val URL_FORGOT_PASSWORD = ROOT + IP_ADDRESS + DIRECTORY + "send_otp.php"
}