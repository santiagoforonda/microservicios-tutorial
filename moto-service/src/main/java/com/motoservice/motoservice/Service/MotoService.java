package com.motoservice.motoservice.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.motoservice.motoservice.Entidades.Moto;
import com.motoservice.motoservice.Repository.MotoRepository;

@Service
public class MotoService {

    @Autowired
    private MotoRepository motoRepo;


    public List<Moto> getAll(){
        return motoRepo.findAll();
    }

    public Optional<Moto> getMotoById(int id){
        return motoRepo.findById(id);
    }

    public Moto save(Moto moto){
        Moto nuevaMoto = motoRepo.save(moto);
        return nuevaMoto;
    }

    public List<Moto> findByUsuarioId(int usuarioId){
        return motoRepo.findByUsuarioId(usuarioId);
    }
    
}
