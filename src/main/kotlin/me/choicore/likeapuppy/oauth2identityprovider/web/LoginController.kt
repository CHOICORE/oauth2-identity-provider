package me.choicore.likeapuppy.oauth2identityprovider.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class LoginController {

    @GetMapping("/login")
    fun signIn(): String {
        return "login"
    }

    @GetMapping
    fun home(): String {
        return "home"
    }
}