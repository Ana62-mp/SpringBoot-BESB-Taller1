package com.krakedev.proyectos.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.repositories.EmpleadoRepository;
import com.krakedev.proyectos.repositories.ProyectoRepository;
import com.krakedev.proyectos.repositories.TareaRepository;

@Service
public class TareaService {

    private final TareaRepository tareaRepository; //final para que no se modifique
    private final ProyectoRepository proyectoRepository;
    private final EmpleadoRepository empleadoRepository;

    public TareaService(
            TareaRepository tareaRepository,
            ProyectoRepository proyectoRepository,
            EmpleadoRepository empleadoRepository) {
        this.tareaRepository = tareaRepository;
        this.proyectoRepository = proyectoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    public Tarea crear(Tarea tarea) {

        Proyecto proyecto = proyectoRepository.findById(tarea.getProyecto().getId())
                .orElseThrow(() -> new RuntimeException(
                        "No existe el proyecto con id: " + tarea.getProyecto().getId()));

        List<Empleado> empleadosEncontrados = new ArrayList<>();

        for (Empleado empleado : tarea.getEmpleados()) {
            Empleado empleadoEncontrado = empleadoRepository.findById(empleado.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "No existe el empleado con id: " + empleado.getId()));

            empleadosEncontrados.add(empleadoEncontrado);
        }

        tarea.setProyecto(proyecto);
        tarea.setEmpleados(empleadosEncontrados);

        return tareaRepository.save(tarea);
    }

    public List<Tarea> listar() {
        return tareaRepository.findAll();
    }

    public Tarea buscarPorId(int id) {
        return tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la tarea con id: " + id));
    }

    public Tarea actualizar(int id, Tarea tarea) {
        Tarea tareaExistente = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la tarea con id: " + id));

        Proyecto proyecto = proyectoRepository.findById(tarea.getProyecto().getId())
                .orElseThrow(() -> new RuntimeException(
                        "No existe el proyecto con id: " + tarea.getProyecto().getId()));

        List<Empleado> empleadosEncontrados = new ArrayList<>();

        for (Empleado empleado : tarea.getEmpleados()) {
            Empleado empleadoEncontrado = empleadoRepository.findById(empleado.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "No existe el empleado con id: " + empleado.getId()));

            empleadosEncontrados.add(empleadoEncontrado);
        }

        tareaExistente.setDescripcion(tarea.getDescripcion());
        tareaExistente.setFechaLimite(tarea.getFechaLimite());
        tareaExistente.setCostoEstimado(tarea.getCostoEstimado());
        tareaExistente.setProyecto(proyecto);
        tareaExistente.setEmpleados(empleadosEncontrados);

        return tareaRepository.save(tareaExistente);
    }

    public boolean eliminar(int id) {
        Tarea tareaExistente = buscarPorId(id);
        
        if (tareaExistente == null) {
			return false;
		}

        tareaRepository.delete(tareaExistente);
        return true;
    }
}