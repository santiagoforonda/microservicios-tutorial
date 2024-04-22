package com.carroservice.Service;

import org.springframework.stereotype.Service;

import com.carroservice.Entities.Carro;
import com.carroservice.Repository.CarroRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CarroService {
    
    @Autowired
    private CarroRepository carroRepo;

    public List<Carro> getAll(){
        return carroRepo.findAll();
    }

    public Optional<Carro> getCarroById(int id){
        return carroRepo.findById(id);
    }

    public Carro save(Carro carro){
        Carro nuevoCarro = carroRepo.save(carro);
        return nuevoCarro;
    }

    public List<Carro> findByUsuario(int usuarioId){
        return carroRepo.findByUsuarioId(usuarioId);
    }
}
