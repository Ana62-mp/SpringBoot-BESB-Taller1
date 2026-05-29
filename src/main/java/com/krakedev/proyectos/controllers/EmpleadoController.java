package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.services.EmpleadoService;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
	
	private final EmpleadoService service;

	public EmpleadoController(EmpleadoService service) {
		super();
		this.service = service;
	}
	

	@PostMapping
	public ResponseEntity<?> guardar(@RequestBody Empleado empleado) {
		try {
			Empleado empleadoGuardado = service.crear(empleado);
			return ResponseEntity.status(HttpStatus.CREATED).body(empleadoGuardado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> listar() {
		try {
			List<Empleado> empleados = service.listar();
			return ResponseEntity.ok(empleados);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al listar empleados");
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable int id) {
		try {
			Empleado empleado = service.buscarPorId(id);

			if (empleado == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
			}

			return ResponseEntity.ok(empleado);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al buscar empleado");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Empleado datos) {
		try {
			Empleado empleadoActualizado = service.actualizar(id, datos);

			if (empleadoActualizado == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
			}

			return ResponseEntity.ok(empleadoActualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		try {
			boolean eliminado = service.eliminar(id);

			if (!eliminado) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
			}

			return ResponseEntity.ok("Empleado eliminado correctamente");
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al eliminar Empleado");
		}
	}
	

}
