package com.example.demo.repository;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Scadenza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScadenzeRepository extends JpaRepository<Scadenza,Integer> {

    Page<Scadenza> findByBeneficiarioContainsIgnoreCase(String denominazione, Pageable pageable);
    @Query("SELECT b FROM Beneficiario b WHERE b.beneficiario = :denominazione AND b.user.id=:id")
    Beneficiario findByBeneficiarioAndIdUser(@Param("denominazione") String denominazione, @Param("id") Integer id);
    @Query("SELECT b FROM Beneficiario b WHERE b.user.id=:id")
    List<Beneficiario> findBeneficiariByIdUser(@Param("id") Integer id);
    @Query("SELECT b FROM Beneficiario b WHERE b.Id = :id")
    Beneficiario findBeneficiarioById(@Param("id") Integer id);
    Page<Scadenza> findByDataScadenza(LocalDate data, Pageable pageable);
    Page<Scadenza> findByDenominazioneContainingIgnoreCase(String beneficiario, Pageable pageable);

    // Totali senza filtro
    @Query("""
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
             where s.status="pagato"
            and   function('year', s.dataScadenza) = :anno
            group by s.denominazione
           """)
    List<CategoriaTotaleView> sumImportoByCategoria(@Param("anno") Integer anno);
    // Totali con filtri data opzionali (funziona in Hibernate/JPA)
    @Query(value = """
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
            where (:dal is null or s.dataScadenza >= :dal)
              and (:al  is null or s.dataScadenza  <= :al)
              and (s.status="pagato")
              and   function('year', s.dataScadenza) = :anno
            group by s.denominazione
           """,
            countQuery = """
          select count(s)
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """
    )
    List<CategoriaTotaleView> sumImportoByCategoriaBetween(
            @Param("dal") LocalDate dal,
            @Param("al")  LocalDate al,
            @Param("anno") Integer anno
    );

    // Totali con filtri data opzionali (funziona in Hibernate/JPA)
    @Query(value = """
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
            where (:dal is null or s.dataScadenza >= :dal)
              and (:al  is null or s.dataScadenza  <= :al)
              and (s.status="pagato")
              and (s.dataScadenza = :dataScadenza)
              and   function('year', s.dataScadenza) = :anno
            group by s.denominazione
           """,
            countQuery = """
          select count(s)
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """)

    List<CategoriaTotaleView> sumImportoByCategoriaAndScadenzaBetween(
            @Param("dal") LocalDate dal,
            @Param("al")  LocalDate al,
            @Param("dataScadenza") LocalDate dataScadenza,
            @Param("anno") Integer anno
    );
    // Totali senza filtro
    @Query(value = """
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
             where s.status="pagato"
             and   s.dataScadenza = :dataScadenza
             and   function('year', s.dataScadenza) = :anno
            group by s.denominazione
           """,
            countQuery = """
          select count(s)
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """
    )
    List<CategoriaTotaleView> sumImportoByCategoriaAndScadenza(
            @Param("dataScadenza") LocalDate dataScadenza,
            @Param("anno") Integer anno);

    // Totali con filtri data opzionali (funziona in Hibernate/JPA)
    @Query(value = """
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
            where (:dal is null or s.dataScadenza >= :dal)
              and (:al  is null or s.dataScadenza  <= :al)
              and (s.status="pagato")
              and (s.denominazione like :beneficiario)
              and   function('year', s.dataScadenza) = :anno
            group by s.denominazione
           """,
            countQuery = """
          select count(s)
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """
    )
    List<CategoriaTotaleView> sumImportoByCategoriaAndBeneficiarioBetween(
            @Param("dal") LocalDate dal,
            @Param("al")  LocalDate al,
            @Param("beneficiario") String beneficiario,
            @Param("anno") Integer anno
    );
    @Query(value = """
           select s.denominazione as categoria,
                  sum(s.importo) as totale
             from Scadenza s
             where s.status="pagato"
             and   s.denominazione like :beneficiario
             and   function('year', s.dataScadenza) = :anno
            group by s.denominazione
           """,
            countQuery = """
          select count(s)
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """
    )

    List<CategoriaTotaleView> sumImportoByCategoriaAndBeneficiario(
            @Param("beneficiario") String beneficiario,
            @Param("anno") Integer anno);


    // 📅 Anni distinti dal campo data (ordina desc). Esclude null.
    @Query("""
           select distinct function('year', s.dataScadenza)
             from Scadenza s
            where s.dataScadenza is not null
            order by function('year', s.dataScadenza) desc
           """)
    List<Integer> findDistinctYears();

    // 📜 Lista scadenze di un anno specifico (paginata)
    @Query(value = """
          select s
            from Scadenza s
            where function('year', s.dataScadenza) = :anno
            and (s.dataScadenza=:data)
          """,
            countQuery = """
          select count(s)
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """)
    Page<Scadenza> findAllByYearAndData(@Param("anno") Integer anno,
                                        @Param("data") LocalDate data,
                                        Pageable pageable);

    // 📜 Lista scadenze di un anno specifico (paginata)
    @Query(value = """
          select s
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
           and s.denominazione=:denominazione
          """,
            countQuery = """
          select count(s)
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """)
    Page<Scadenza> findAllByYearAndBeneficiario(@Param("anno") Integer anno,
                                                @Param("denominazione") String denominazione,
                                                Pageable pageable);
    // 📜 Lista scadenze di un anno specifico (paginata)
    @Query(
            value = """
          select s
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """,
            countQuery = """
          select count(s)
            from Scadenza s
           where function('year', s.dataScadenza) = :anno
          """
    )
    Page<Scadenza> findAllByYear(@Param("anno") Integer anno, Pageable pageable);

    // 📊 Totali per categoria in un anno
    @Query("""
           select s.denominazione as categoria, sum(s.importo) as totale
             from Scadenza s
            where function('year', s.dataScadenza) = :anno
            group by s.denominazione
            order by s.denominazione
           """)
    List<CategoriaTotaleView> sumImportoByCategoriaForYear(@Param("anno") Integer anno);
}

