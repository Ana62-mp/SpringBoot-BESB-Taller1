package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.services.ProyectoService;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(
	    origins = "http://localhost:5173",
	    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
	    allowedHeaders = {"Authorization", "Content-Type"}
	)

public class ProyectoController {
	private final ProyectoService service;

	public ProyectoController(ProyectoService service) {
		super();
		this.service = service;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> guardar(@RequestBody Proyecto proyecto) {
		try {
			Proyecto proyectoGuardado = service.crear(proyecto);
			return ResponseEntity.status(HttpStatus.CREATED).body(proyectoGuardado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping
	public ResponseEntity<?> listar() {
		try {
			List<Proyecto> proyectos = service.listar();
			return ResponseEntity.ok(proyectos);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al listar proyectos");
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable int id) {
		try {
			Proyecto proyecto = service.buscarPorId(id);

			if (proyecto == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}

			return ResponseEntity.ok(proyecto);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al buscar empleado");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Proyecto datos) {
		try {
			Proyecto proyectoActualizado = service.actualizar(id, datos);

			if (proyectoActualizado == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}

			return ResponseEntity.ok(proyectoActualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		try {
			boolean eliminado = service.eliminar(id);

			if (!eliminado) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}

			return ResponseEntity.ok("Proyecto eliminado correctamente");
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al eliminar Proyecto");
		}
	}
	
	@GetMapping("/publico/resumen")
	public ResponseEntity<?> resumenPublico() {
	    Long total = service.contarProyectos();
	    return ResponseEntity.ok(total);
	}

}
