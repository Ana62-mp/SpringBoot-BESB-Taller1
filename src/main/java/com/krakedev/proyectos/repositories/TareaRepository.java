package com.krakedev.proyectos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krakedev.proyectos.entidades.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByProyectoId(int proyectoId);

}