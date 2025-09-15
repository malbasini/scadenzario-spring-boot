package com.example.demo.repository;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Scadenza;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScadenzeRepository extends JpaRepository<Scadenza,Integer> {

    Page<Scadenza> findByBeneficiarioContainsIgnoreCase(String denominazione, Pageable pageable);

    @Transactional
    @Query("SELECT b FROM Beneficiario b WHERE b.beneficiario = :denominazione AND b.user.id=:id")
    Beneficiario findByBeneficiarioAndIdUser(@Param("denominazione") String denominazione, @Param("id") Integer id);

    @Transactional
    @Query("SELECT b FROM Beneficiario b WHERE b.user.id=:id")
    List<Beneficiario> findBeneficiariByIdUser(@Param("id") Integer id);

    @Query("SELECT b FROM Beneficiario b WHERE b.Id = :id")
    Beneficiario findBeneficiarioById(@Param("id") Integer id);

    Page<Scadenza> findByDataScadenzaContainsIgnoreCase(LocalDate data, Pageable pageable);

    Page<Scadenza> findByDataScadenza(LocalDate data, Pageable pageable);

    Page<Scadenza> findByDenominazione(String beneficiario, Pageable pageable);

    Page<Scadenza> findByDenominazioneContainingIgnoreCase(String beneficiario, Pageable pageable);

    // Totali senza filtro
    @Query("""
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
             where s.status="pagato"
            group by s.denominazione
            order by s.denominazione
           """)
    List<CategoriaTotaleView> sumImportoByCategoria();

    // Totali con filtri data opzionali (funziona in Hibernate/JPA)
    @Query("""
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
            where (:dal is null or s.dataScadenza >= :dal)
              and (:al  is null or s.dataScadenza  <= :al)
              and (s.status="pagato")
            group by s.denominazione
            order by s.denominazione
           """)
    List<CategoriaTotaleView> sumImportoByCategoriaBetween(
            @Param("dal") LocalDate dal,
            @Param("al")  LocalDate al
    );

    // Totali con filtri data opzionali (funziona in Hibernate/JPA)
    @Query("""
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
            where (:dal is null or s.dataScadenza >= :dal)
              and (:al  is null or s.dataScadenza  <= :al)
              and (s.status="pagato")
              and (s.dataScadenza = :dataScadenza)
            group by s.denominazione
            order by s.denominazione
           """)

    List<CategoriaTotaleView> sumImportoByCategoriaAndScadenzaBetween(
            @Param("dal") LocalDate dal,
            @Param("al")  LocalDate al,
            @Param("dataScadenza") LocalDate dataScadenza
    );

    // Totali senza filtro
    @Query("""
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
             where s.status="pagato"
             and   s.dataScadenza = :dataScadenza
            group by s.denominazione
            order by s.denominazione
           """)
    List<CategoriaTotaleView> sumImportoByCategoriaAndScadenza(@Param("dataScadenza") LocalDate dataScadenza);

    // Totali con filtri data opzionali (funziona in Hibernate/JPA)
    @Query("""
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
            where (:dal is null or s.dataScadenza >= :dal)
              and (:al  is null or s.dataScadenza  <= :al)
              and (s.status="pagato")
              and (s.denominazione like :beneficiario)
              and (lower(s.denominazione) like lower(concat('%', :beneficiario, '%')))
              and (upper(s.denominazione) like upper(concat('%', :beneficiario, '%')))
            group by s.denominazione
            order by s.denominazione
           """)

    List<CategoriaTotaleView> sumImportoByCategoriaAndBeneficiarioBetween(
            @Param("dal") LocalDate dal,
            @Param("al")  LocalDate al,
            @Param("beneficiario") String beneficiario
    );

    @Query("""
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
             where s.status="pagato"
             and   s.denominazione like :beneficiario
             and (lower(s.denominazione) like lower(concat('%', :beneficiario, '%')))
             and (upper(s.denominazione) like upper(concat('%', :beneficiario, '%')))
            group by s.denominazione
            order by s.denominazione
           """)
    List<CategoriaTotaleView> sumImportoByCategoriaAndBeneficiario(@Param("beneficiario") String beneficiario);


















}
