package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.repositories.EmpleadoRepository;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado crear(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public List<Empleado> listar() {
        return empleadoRepository.findAll();
    }

    public Empleado buscarPorId(int id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el empleado con id: " + id));
    }

    public Empleado actualizar(int id, Empleado empleado) {
        Empleado empleadoExistente = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el empleado con id: " + id));

        empleadoExistente.setNombre(empleado.getNombre());
        empleadoExistente.setCargo(empleado.getCargo());

        return empleadoRepository.save(empleadoExistente);
    }

    public boolean eliminar(int id) {
        Empleado empleadoExistente = buscarPorId(id);
        if (empleadoExistente == null) {
			return false;
		}

        empleadoRepository.delete(empleadoExistente);
        return true;
    }
}