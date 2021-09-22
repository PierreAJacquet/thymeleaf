package com.ipiecoles.communes.web;

import com.ipiecoles.communes.web.model.Commune;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

public class CommuneTest {


    @Test
    public void testCommuneOK(){
        //Given
        Commune commune = new Commune("01011", "Apremont", "01100", 46.2054981558, 5.65781475272);

        //When
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Commune>> violations = validator.validate(commune);

        //Then
        Assertions.assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "'XXX', 'Apremont', '01100', 46.2054981558, 5.65781475272, 'Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)'",
            "'ASDCV', 'Apremont', '01100', 46.2054981558, 5.65781475272, 'Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)'",
            "'A0101', 'Apremont', '01100', 46.2054981558, 5.65781475272, 'Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)'",
            "'0C120', 'Apremont', '01100', 46.2054981558, 5.65781475272, 'Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)'",
            "'0101', 'Apremont', '01100', 46.2054981558, 5.65781475272, 'Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)'",
            "'010111', 'Apremont', '01100', 46.2054981558, 5.65781475272, 'Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)'",
            ", 'Apremont', '01100', 46.2054981558, 5.65781475272, 'ne doit pas être nul'",
            "'', 'Apremont', '01100', 46.2054981558, 5.65781475272, 'Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)'",
    })
    public void testCommuneValidatorCodeInsee(String codeInsee, String nom, String codePostal, Double latitude, Double longitude, String error){
        //Given
        Commune commune = new Commune(codeInsee, nom, codePostal, latitude, longitude);

        //When
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Commune>> violations = validator.validate(commune);

        //Then
        Assertions.assertThat(violations).hasSizeGreaterThan(0);
        Assertions.assertThat(violations.stream().map(ConstraintViolation::getMessage).toArray()).contains(error);
    }

    @ParameterizedTest
    @CsvSource({
            "'01011', 'Apremont?', '01100', 46.2054981558, 5.65781475272, 'Le nom de la commune ne peut contenir que des lettres, des tirets, des espaces et éventuellement le numéro d'arrondissement'",
            "'01011', 'Paris 123', '01100', 46.2054981558, 5.65781475272, 'Le nom de la commune ne peut contenir que des lettres, des tirets, des espaces et éventuellement le numéro d'arrondissement'",
            "'01011', 'Âpremont', '01100', 46.2054981558, 5.65781475272, 'Le nom de la commune ne peut contenir que des lettres, des tirets, des espaces et éventuellement le numéro d'arrondissement'",
            "'01011', '', '01100', 46.2054981558, 5.65781475272, 'Le nom de la commune ne peut contenir que des lettres, des tirets, des espaces et éventuellement le numéro d'arrondissement'",
            "'01011',, '01100', 46.2054981558, 5.65781475272, 'ne doit pas être nul'",
    })
    public void testCommuneValidatorNom(String codeInsee, String nom, String codePostal, Double latitude, Double longitude, String error){
        //Given
        Commune commune = new Commune(codeInsee, nom, codePostal, latitude, longitude);

        //When
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Commune>> violations = validator.validate(commune);

        //Then
        Assertions.assertThat(violations).hasSizeGreaterThan(0);
        Assertions.assertThat(violations.stream().map(communeConstraintViolation -> "'"+communeConstraintViolation.getMessage() + "'")).containsAnyOf(error, "'"+error+"'");
    }
    @ParameterizedTest
    @CsvSource({
            "'01011', 'Apremont', 'XXX', 46.2054981558, 5.65781475272, 'Le code postal doit contenir 5 chiffres'",
            "'01011', 'Apremont', 'ASDCV', 46.2054981558, 5.65781475272, 'Le code postal doit contenir 5 chiffres'",
            "'01011', 'Apremont', 'A0101', 46.2054981558, 5.65781475272, 'Le code postal doit contenir 5 chiffres'",
            "'01011', 'Apremont', '0C120', 46.2054981558, 5.65781475272, 'Le code postal doit contenir 5 chiffres'",
            "'01011', 'Apremont', '0101', 46.2054981558, 5.65781475272, 'Le code postal doit contenir 5 chiffres'",
            "'01011', 'Apremont', '010111', 46.2054981558, 5.65781475272, 'Le code postal doit contenir 5 chiffres'",
            "'01011', 'Apremont',, 46.2054981558, 5.65781475272, 'ne doit pas être nul'",
            "'01011', 'Apremont', '', 46.2054981558, 5.65781475272, 'Le code postal doit contenir 5 chiffres'",
    })
    public void testCommuneValidatorCodePostal(String codeInsee, String nom, String codePostal, Double latitude, Double longitude, String error){
        //Given
        Commune commune = new Commune(codeInsee, nom, codePostal, latitude, longitude);

        //When
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Commune>> violations = validator.validate(commune);

        //Then
        Assertions.assertThat(violations).hasSizeGreaterThan(0);
        Assertions.assertThat(violations.stream().map(ConstraintViolation::getMessage).toArray()).contains(error);
    }

    @ParameterizedTest
    @CsvSource({
            "'01011', 'Apremont', '01100', -91, 5.65781475272, 'doit être supérieur ou égal à -90'",
            "'01011', 'Apremont', '01100', 91, 5.65781475272, 'doit être inférieur ou égal à 90'",
            "'01011', 'Apremont', '01100', 46.2054981558, -181, 'doit être supérieur ou égal à -180'",
            "'01011', 'Apremont', '01100', 46.2054981558, 181, 'doit être inférieur ou égal à 180'",
    })
    public void testCommuneValidatorLatitudeLongitude(String codeInsee, String nom, String codePostal, Double latitude, Double longitude, String error){
        //Given
        Commune commune = new Commune(codeInsee, nom, codePostal, latitude, longitude);

        //When
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Commune>> violations = validator.validate(commune);

        //Then
        Assertions.assertThat(violations).hasSizeGreaterThan(0);
        Assertions.assertThat(violations.stream().map(ConstraintViolation::getMessage).toArray()).contains(error);
    }
}
