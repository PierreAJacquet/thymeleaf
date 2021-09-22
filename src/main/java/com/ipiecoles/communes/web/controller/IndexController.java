package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    private static final List<String> communeObjectFieldName = Arrays.stream(Commune.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());

    private static final Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    @Autowired
    private CommuneRepository communeRepository;

    @GetMapping(value = "/")
    public String listeCommunes(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                @RequestParam(defaultValue = "10") Integer size,
                                @RequestParam(defaultValue = "codeInsee") String sortProperty,
                                @RequestParam(defaultValue = "ASC") String sortDirection,
                                @RequestParam(required = false, defaultValue = "") String search,
                                @RequestParam(required = false) Boolean successfulConnection,
                                final ModelMap model) {
        long nbCommunes = communeRepository.count();
        model.put("nbCommunes", nbCommunes);

        if (Boolean.TRUE.equals(successfulConnection)) {
            model.addAttribute("type", "success");
            model.addAttribute("message", "La connection a réussi");
        }

        //On vérifie la validité des arguments
        checkParametersValidity(nbCommunes, size, page, sortDirection, sortProperty);


        //Constituer un PageRequest
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty);
        Page<Commune> communes;
        if (search == null || search.isEmpty()) {
            //Appeler findAll si search est null
            communes = communeRepository.findAll(pageRequest);
        } else if (isCodeInsee(search)) {
            //Dans le cas où la valeur de search est un code Insee
            return "redirect:/communes/" + search;
        } else {
            //Appeler findByNomContainingIgnoreCase si search n'est pas null
            communes = communeRepository.findByNomContainingIgnoreCase(search, pageRequest);
        }

        model.put("communes", communes);
        model.put("nbCommunes", communes.getTotalElements());
        model.put("pageSizes", Arrays.asList("5", "10", "20", "50", "100"));
        //Affichage des communes de 1 à 10 => page = 0 et size = 10
        //Affichage des communes de 11 à 20 => page = 1 et size = 10
        //Affichage des communes de 41 à 60 => page = 2 et size = 20
        model.put("start", page * size + 1);//A remplacer par la valeur dynamique
        model.put("end", (page + 1) * size);//A remplacer par la valeur dynamique
        model.put("page", page);
        model.put("search", search);
        model.put("size", size);
        model.put("sortProperty", sortProperty);
        model.put("sortDirection", sortDirection);

        model.put("fragment", "listeCommunes");
        model.put("template", "listeCommunes");
        return "main";
    }

    private void checkParametersValidity(
            final long nbCommunes,
            Integer size,
            Integer page,
            String sortDirection,
            String sortProperty
    ) {
        if (size < 1 && size > nbCommunes) {
            throw new IllegalArgumentException("La valeur du paramètre size n'est pas correcte");
        }

        if (page < 0 && page > nbCommunes / size) {
            throw new IllegalArgumentException("La valeur du paramètre page n'est pas correcte");
        }

        if (!Arrays.asList("ASC", "DESC").contains(sortDirection)) {
            throw new IllegalArgumentException("La valeur du paramètre sortDirection n'est pas correcte");
        }

        if (!communeObjectFieldName.contains((sortProperty))) {
            throw new IllegalArgumentException("La valeur du paramètre sortProperty n'est pas correcte");
        }
    }

    private boolean isCodeInsee(String s) {
        return s.length() == 5 && numberPattern.matcher(s).matches();
    }
}
