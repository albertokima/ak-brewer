package com.viki.brewer.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SegurancaController {
	
	private static final String urlPaginaLogin = "Login";
	private static final String urlPaginaPrincipal = "redirect:/";
	
	@GetMapping("/login")
	public String login(@AuthenticationPrincipal User user) {
		if (user != null) {
			return urlPaginaPrincipal;
		}
		
		return urlPaginaLogin;
	}
	
}
