package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.entity.Identifiable;
import com.group1.proyect.freshbasket.repository.GenericRepository;
import com.group1.proyect.freshbasket.service.GenericService;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericServiceImpl<E extends Identifiable<ID>, REQ, RES, ID> implements GenericService<E, REQ, RES, ID> {

    protected final GenericRepository<E, ID> repository;

    protected GenericServiceImpl(GenericRepository<E, ID> repository) {
        this.repository = repository;
    }

    protected abstract RES convertToResponseDto(E entity);
    protected abstract E convertToEntity(REQ requestDto);
    protected abstract void updateEntityFromDto(REQ requestDto, E entity);

    @Override
    @Transactional(readOnly = true)
    public List<RES> getAll() {
        return repository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RES getById(ID id) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado con ID: " + id));
        return convertToResponseDto(entity);
    }

    @Override
    @Transactional
    public RES create(REQ requestDto) {
        E entity = convertToEntity(requestDto);
        E savedEntity = repository.save(entity);
        return convertToResponseDto(savedEntity);
    }

    @Override
    @Transactional
    public RES update(ID id, REQ requestDto) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar, ID inexistente: " + id));
        updateEntityFromDto(requestDto, entity);
        E updatedEntity = repository.save(entity);
        return convertToResponseDto(updatedEntity);
    }

    @Override
    @Transactional
    public void delete(ID id) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el registro que deseas eliminar"));
        repository.delete(entity);
    }
}