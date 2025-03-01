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

    @Autowired
    private RicevutaService ricevutaService;

    @Autowired
    private ScadenzaService scadenzaService;


    @GetMapping("/{id}/ricevuta")
    public String deleteRicevuta(@PathVariable("id") Integer id){
        ricevutaService.deleteById(id);
        return "redirect:/scadenze/list?message=Ricevuta eliminata con successo";
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

            return ResponseEntity.ok("File salvato con successo nel database.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore durante il salvataggio del file.");
        }
    }



}
