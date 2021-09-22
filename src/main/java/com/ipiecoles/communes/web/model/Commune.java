package com.ipiecoles.communes.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Entity
public class Commune {

    public static final String REGEX_CODE_INSEE = "^[0-9]{1}[0-9AB]{1}[0-9]{3}$";
    public static final String REGEX_CODE_POSTAL = "^[0-9]{5}$";
    public static final String REGEX_NOM_COMMUNE = "^[A-Za-z-' ]+[0-9]{0,2}$";
    public static final String REGEX_LATITUDE = "^[0-9]{0,3}[.]{1}[0-9]{0,11}$";


    @Id
    @Column(length = 5)
    @Pattern(regexp = REGEX_CODE_INSEE, message = "Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)")
    @NotNull
    private String codeInsee;

    @Pattern(regexp = REGEX_NOM_COMMUNE, message = "Le nom de la commune ne peut contenir que des lettres, des tirets, des espaces et éventuellement le numéro d\'arrondissement")
    @NotNull
    private String nom;

    @Column(length = 5)
    @Pattern(regexp = REGEX_CODE_POSTAL, message = "Le code postal doit contenir 5 chiffres")
    @NotNull
    private String codePostal;

    @Min(value = -90, message = "doit être supérieur ou égal à -90")
    @Max(value = 90, message = "doit être inférieur ou égal à 90")
    @NotNull
    private Double latitude;

    @Min(value = -180, message = "doit être supérieur ou égal à -180")
    @Max(value = 180, message = "doit être inférieur ou égal à 180")
    @NotNull
    private Double longitude;

    public Commune() {
    }

    public Commune(String codeInsee, String nom, String codePostal, Double latitude, Double longitude) {
        this.codeInsee = codeInsee;
        this.nom = nom;
        this.codePostal = codePostal;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCodeInsee() {
        return codeInsee;
    }

    public void setCodeInsee(String codeInsee) {
        this.codeInsee = codeInsee;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getDistance(Double latitude, Double longitude) {
        Double lat1 = Math.toRadians(latitude);
        Double lng1 = Math.toRadians(longitude);
        Double lat2 = Math.toRadians(this.latitude);
        Double lng2 = Math.toRadians(this.longitude);

        double dlon = lng2 - lng1;
        double dlat = lat2 - lat1;

        double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(6371.009 * c);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Commune{");
        sb.append("codeInsee='").append(codeInsee).append('\'');
        sb.append(", nom='").append(nom).append('\'');
        sb.append(", codePostal='").append(codePostal).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}

