package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CommuneController {

    private static final double DEGRE_LAT_KM = 111d;
    private static final double DEGRE_LONG_KM = 77d;

    @Autowired
    private CommuneRepository communeRepository;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/communes/{codeInsee}")
    public String getCommune(
            @PathVariable String codeInsee,
            @RequestParam(defaultValue = "10") Integer perimetre,
            final ModelMap model) {
        Commune commune = communeRepository.findById(codeInsee)
                .orElseThrow(() -> new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee));

        //Récupérer les communes proches de celle-ci
        model.put("commune", commune);
        model.put("perimetre", perimetre);

        //Si le périmètre dépasse 20km
        if (perimetre > 20) {
            model.addAttribute("type", "danger");
            model.addAttribute("message", "Le périmètre de recherche ne peut pas dépasser les 20 km");
        } else {
            model.put("communesProches", this.findCommunesProches(commune, perimetre));
        }
        model.put("newCommune", false);

        model.put("templateDetail", "detail");
        model.put("fragmentDetail", "fragDetail");

        return "detail";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveNewCommune(final ModelMap model,
                                 @Valid Commune commune,
                                 final BindingResult result,
                                 RedirectAttributes attributes) {
        if (!result.hasErrors()) {
            commune = communeRepository.save(commune);
            model.put("commune", commune);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Enregistrement de la commune " + commune.getNom() + " effectuée !");
            return "redirect:/communes/" + commune.getCodeInsee();
        } else {
            model.addAttribute("type", "danger");
            model.addAttribute("message", "Erreur lors de la sauvegarde de la commune");
            return "detail";
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/communes/{codeInsee}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveExistingCommune(
            final ModelMap model,
            RedirectAttributes attributes,
            @PathVariable String codeInsee,
            @Valid Commune commune,
            final BindingResult result) {
        if (!result.hasErrors()) {
            if (commune.getCodeInsee().isEmpty()) {
                throw new EntityNotFoundException("Le code INSEE de la commune est obligatoire");
            }

            commune = communeRepository.save(commune);


            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Enregistrement de la commune " + commune.getNom() + " effectuée !");
            return "redirect:/communes/" + commune.getCodeInsee();
        }
        //S'il y a des erreurs...
        //Possibilité 1 : Rediriger l'utilisateur vers la page générique d'erreur
        //Possibilité 2 : Laisse sur la même page en affichant les erreurs pour chaque champ
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la sauvegarde de la commune");
        return "detail";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/communes/new")
    public String newCommune(
            final ModelMap model) {
        model.put("commune", new Commune());
        model.put("newCommune", true);
        return "detail";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/communes/{codeInsee}/delete")
    public String deleteCommune(@PathVariable String codeInsee,
                                RedirectAttributes attributes) {
        try {
            communeRepository.deleteById(codeInsee);
        } catch (Exception e) {
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
        }
        attributes.addFlashAttribute("type", "success");
        attributes.addFlashAttribute("message", "Suppression de la commune effectuée");
        return "redirect:/";
    }

    /**
     * Récupère une liste des communes dans un périmètre autour d'une commune
     *
     * @param commune       La commune sur laquelle porte la recherche
     * @param perimetreEnKm Le périmètre de recherche en kilomètre
     * @return La liste des communes triées de la plus proche à la plus lointaine
     */
    private List<Commune> findCommunesProches(Commune commune, Integer perimetreEnKm) {
        Double latMin, latMax, longMin, longMax, degreLat, degreLong;
        //1 degré latitude = 111km, 1 degré longitude = 77km
        degreLat = perimetreEnKm / DEGRE_LAT_KM;
        degreLong = perimetreEnKm / DEGRE_LONG_KM;
        latMin = commune.getLatitude() - degreLat;
        latMax = commune.getLatitude() + degreLat;
        longMin = commune.getLongitude() - degreLong;
        longMax = commune.getLongitude() + degreLong;
        List<Commune> communesProches = communeRepository.findByLatitudeBetweenAndLongitudeBetween(latMin, latMax, longMin, longMax);

        return communesProches.stream().
                filter(commune1 -> !commune1.getNom().equals(commune.getNom()) && commune1.getDistance(commune.getLatitude(), commune.getLongitude()) <= perimetreEnKm).
                sorted(Comparator.comparing(o -> o.getDistance(commune.getLatitude(), commune.getLongitude()))).
                collect(Collectors.toList());
    }

}
