package com.krakedev.proyectos.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;
import com.krakedev.proyectos.security.JwtUtil;
import com.krakedev.proyectos.services.TokenBlacklistService;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
	    origins = "http://localhost:5173",
	    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
	    allowedHeaders = {"Authorization", "Content-Type"}
	)
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
			if(usuarioRepository.existsByUsername(usuario.getUsername())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("El usuario ya existe. Intente otro username");
			}
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

	        Usuario usuario = usuarioService.login(username, password);

	        if (usuario != null) {
	            String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

	            return ResponseEntity.ok(Map.of(
	                "token", token,
	                "username", usuario.getUsername(),
	                "rol", usuario.getRol()
	            ));
	        } else {
	            return ResponseEntity
	                    .status(HttpStatus.UNAUTHORIZED)
	                    .body("Usuario o contraseña incorrecta");
	        }

	    } catch (Exception e) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al iniciar sesión: " + e.getMessage());
	    }
	}

	@GetMapping("/perfil")
	public ResponseEntity<?> perfil(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Acceso Denegado: Debes proveer un token Bearer valido en la cabecera Authorization");
		}

		String token = authHeader.substring(7);

		if (blackListService.estaInvalidado(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Acceso Denegado: El token ya fue invalidado por logout");
		}

		DecodedJWT datosToken = JwtUtil.validarToken(token);

		if (datosToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso Denegado: Token invalido o expirado");
		}

		String usuario = datosToken.getSubject();

		String rol = datosToken.getClaim("rol").asString();

		return ResponseEntity.ok(Map.of("Mensaje", "Bienvenido al sistema protegido por jwt", "Usuario", usuario, "Rol",
				rol, "Estatus", "Autenticado exitosamente"));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			DecodedJWT datosToken = JwtUtil.validarToken(token);
			if (datosToken == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalido o expirado");
			}
			blackListService.invalidarToken(token);
			return ResponseEntity.ok(Map.of("Mensaje", "Sesión cerrada exitosamente. Token invalidado"));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalido. Sesion no cerrada.");

	}
	
	@GetMapping("/dashboard")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> adminDashboard() {

	    String usuario = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    return ResponseEntity.ok(Map.of(
	            "Mensaje", "Bienvenido al panel secreto de administradores",
	            "admin", usuario
	    ));
	}

}
