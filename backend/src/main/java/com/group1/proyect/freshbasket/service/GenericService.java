package com.group1.proyect.freshbasket.service;

import java.util.List;

public interface GenericService<E, REQ, RES, ID> {
    List<RES> getAll();
    RES getById(ID id);
    RES create(REQ requestDto);
    RES update(ID id, REQ requestDto);
    void delete(ID id);
}