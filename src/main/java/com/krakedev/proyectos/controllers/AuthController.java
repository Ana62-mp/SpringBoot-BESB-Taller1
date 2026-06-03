package com.krakedev.proyectos.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;
import com.krakedev.proyectos.security.JwtUtil;
import com.krakedev.proyectos.services.TokenBlacklistService;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuarioService usuarioService;
	private final UsuarioRepository usuarioRepository;
	private final TokenBlacklistService blackListService;

	public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
			TokenBlacklistService blackListService) {
		super();
		this.usuarioService = usuarioService;
		this.usuarioRepository = usuarioRepository;
		this.blackListService = blackListService;

	}

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioRegistrado = usuarioService.registrar(usuario);

			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar el usuario: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
		try {
			String username = credenciales.get("username");
			String password = credenciales.get("password");

			boolean autenticado = usuarioService.autenticar(username, password);

			if (autenticado) {
				Usuario usuario = usuarioRepository.findByUsername(username).get();

				String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

				return ResponseEntity.ok(Map.of("token", token));
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrecta");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al iniciar sesión: " + e.getMessage());
		}
	}

	@GetMapping("/perfil")
	public ResponseEntity<?> perfil(Authentication authentication) {
		//validacion
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(401).body("Usuario no autenticado de forma oficial.");
		}
		
		//creacion de variables para Map
		String rolAutorizado = authentication.getAuthorities().iterator().next().getAuthority();
		String username = authentication.getName();
		
		//Crear Map y ponerle datos
		java.util.Map<String, String> perfilMap = new java.util.HashMap<>();
		perfilMap.put("username", username);
		perfilMap.put("rol_asignado", rolAutorizado.replace("ROLE_", ""));
		perfilMap.put("estado", "Token verificado mediante Spring Security Context.");

		return ResponseEntity.ok(perfilMap);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			blackListService.invalidarToken(token);
			DecodedJWT datosToken = JwtUtil.validarToken(token);
			if (datosToken == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalido o expirado");
			}
			return ResponseEntity.ok(Map.of("Mensaje", "Sesión cerrada exitosamente. Token invalidado"));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalido. Sesion cerrada.");

	}

}
