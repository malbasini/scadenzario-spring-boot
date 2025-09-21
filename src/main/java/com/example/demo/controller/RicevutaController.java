package com.example.demo.controller;

import com.example.demo.model.Ricevuta;
import com.example.demo.service.RicevutaService;
import java.util.Optional;
import com.example.demo.service.ScadenzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping
public class RicevutaController {

    private final RicevutaService ricevutaService;
    private final ScadenzaService scadenzaService;

    public RicevutaController(RicevutaService ricevutaService, ScadenzaService scadenzaService) {
        this.ricevutaService = ricevutaService;
        this.scadenzaService = scadenzaService;
    }

    @PostMapping("/deletericevuta")
    public String deleteRicevuta(@RequestParam("id") Integer id)
    {
        Ricevuta s = ricevutaService.findById(id);
        int identificativo = s.getScadenza().getId();
        ricevutaService.deleteById(id);
        return "redirect:/" + identificativo + "/editscadenza" + "?message1= Ricevuta eliminata con successo!";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") Integer id) {
        Ricevuta fileEntity = Optional.ofNullable(ricevutaService.findById(id))
                .orElseThrow(() -> new RuntimeException("File non trovato con ID: " + id));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileEntity.getNomeFile() + "\"")
                .body(fileEntity.getContent()); // Invia i dati come array di byte
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("id") Integer id) {
        try {
            // Converti il file in array di byte
            byte[] fileData = file.getBytes();

            // Crea un nuovo oggetto FileEntity con il nome del file e i dati
            Ricevuta fileEntity = new Ricevuta();
            fileEntity.setNomeFile(file.getOriginalFilename());
            fileEntity.setContent(fileData);
            fileEntity.setTipoFile(file.getContentType());
            fileEntity.setScadenza(scadenzaService.findById(id));
            // Salva l'entità nel database
            ricevutaService.save(fileEntity);
            return ResponseEntity.ok("File salvato con successo nel database. Ricaricare la pagina per visualizzare il file.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore durante il salvataggio del file.");
        }
    }
}