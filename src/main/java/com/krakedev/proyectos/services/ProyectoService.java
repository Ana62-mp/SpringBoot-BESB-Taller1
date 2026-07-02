package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.repositories.ProyectoRepository;
import com.krakedev.proyectos.repositories.TareaRepository;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;

    public ProyectoService(ProyectoRepository proyectoRepository, TareaRepository tareaRepository) {
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
    }

    public Proyecto crear(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    public List<Proyecto> listar() {
        return proyectoRepository.findAll();
    }

    public Proyecto buscarPorId(int id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el proyecto con id: " + id));
    }

    public Proyecto actualizar(int id, Proyecto proyecto) {
        Proyecto proyectoExistente = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el proyecto con id: " + id));

        proyectoExistente.setNombre(proyecto.getNombre());
        proyectoExistente.setDescripcion(proyecto.getDescripcion());
        proyectoExistente.setFechaInicio(proyecto.getFechaInicio());

        return proyectoRepository.save(proyectoExistente);
    }

    public boolean eliminar(int id) {
        Proyecto proyectoExistente = proyectoRepository.findById(id).orElse(null);

        if (proyectoExistente == null) {
            return false;
        }

        List<Tarea> tareasDelProyecto = tareaRepository.findByProyectoId(id);

        for (Tarea tarea : tareasDelProyecto) {
            tarea.getEmpleados().clear();
            tareaRepository.save(tarea);
            tareaRepository.delete(tarea);
        }

        proyectoRepository.delete(proyectoExistente);

        return true;
    }
    
    public Long contarProyectos() {
        return proyectoRepository.count();
    }
}