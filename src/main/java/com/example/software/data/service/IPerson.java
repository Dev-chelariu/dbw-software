package com.example.software.data.service;

import com.example.software.data.entity.Employee;

import java.util.List;

/**
 *  Generics interface
 * @param <T> Object you want to use
 * @param <ID> id from that object
 */
public interface IPerson<T, ID> {

    List<T> findAll(String stringFilter);

    T findById(ID id);

    void save(T object);

    void delete(T object);

    int count();

    List<Employee>find();
}



