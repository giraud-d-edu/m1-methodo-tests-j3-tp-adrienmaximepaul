package com.ynov.testing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@service
public class CalculatriceService {

    
    private final CalculatriceRepository calculatriceRepository;

    @Autowired
    public CalculatriceService(CalculatriceRepository calculatriceRepository) {
        this.calculatriceRepository = calculatriceRepository;
    }

}