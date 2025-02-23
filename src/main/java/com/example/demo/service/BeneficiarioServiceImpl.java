package com.example.demo.service;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Register;
import com.example.demo.repository.BeneficiariRepository;
import com.example.demo.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BeneficiarioServiceImpl implements BeneficiarioService {
    @Autowired
    private BeneficiariRepository beneficiariRepository;
    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public Beneficiario save(Beneficiario beneficiario) {
        return beneficiariRepository.save(beneficiario);
    }
    @Override
    public List<Beneficiario> findAll() {
        return beneficiariRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        beneficiariRepository.deleteById(id);
    }
    @Override
    public Beneficiario findById(int id) {
        return beneficiariRepository.findById(id).orElseThrow();
    }
    @Override
    public Beneficiario update(Beneficiario beneficiario) {
        return beneficiariRepository.save(beneficiario);
    }

    public Page<Beneficiario> findBeneficiari(int page, int size, String beneficiario, String sortBy, String sortDirection) {
        Sort sort = Sort.by(sortBy);
        sort = sortDirection.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        // Filtro per titolo
        if (beneficiario != null && !beneficiario.isEmpty()) {
            return beneficiariRepository.findByBeneficiarioContainsIgnoreCase(beneficiario, pageable);
        }
        return beneficiariRepository.findAll(pageable);
    }
    public Beneficiario existsByBeneficiarioAndIdUser(String beneficiario, Register user){
        return beneficiariRepository.findByBeneficiarioAndIdUser(beneficiario,user.getId());
    }
}
