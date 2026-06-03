package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.services.TareaService;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {
	private final TareaService service;

	public TareaController(TareaService service) {
		super();
		this.service = service;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> guardar(@RequestBody Tarea tarea) {
		try {
			Tarea tareaGuardado = service.crear(tarea);
			return ResponseEntity.status(HttpStatus.CREATED).body(tareaGuardado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping
	public ResponseEntity<?> listar() {
		try {
			List<Tarea> tareas = service.listar();
			return ResponseEntity.ok(tareas);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al listar tareas");
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable int id) {
		try {
			Tarea tarea = service.buscarPorId(id);

			if (tarea == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrado");
			}

			return ResponseEntity.ok(tarea);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al buscar Tarea");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Tarea datos) {
		try {
			Tarea tareaActualizado = service.actualizar(id, datos);

			if (tareaActualizado == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrado");
			}

			return ResponseEntity.ok(tareaActualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		try {
			boolean eliminado = service.eliminar(id);

			if (!eliminado) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrado");
			}

			return ResponseEntity.ok("Tarea eliminado correctamente");
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al eliminar Tarea");
		}
	}
}
