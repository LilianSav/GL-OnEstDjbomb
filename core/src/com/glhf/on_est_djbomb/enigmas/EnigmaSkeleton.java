package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class EnigmaSkeleton {

    private int solution;
    private String indice;// texte d'indice
    private boolean isHost;
    private int tpsUtilise;
    private String nom;// nom de l'énigme
    private int tpsBeforeIndice;// nombre de secondes minimum avant d'autoriser l'utilisateur à afficher l'indice
    private int tpsBeforeSolution;// nombre de secondes minimum avant d'autoriser l'utilisateur à afficher la solution
    private String titreTable;// phrase composée du nom de l'énigme et d'une explication lorsque nécessaire, elle est affiché en haut du table

    public EnigmaSkeleton(boolean isHost) {
        super();
        setHost(isHost);
    }

    // Path de l'image à afficher
    public abstract void load(Table enigmaManagerTable);

    public abstract void unload();

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }

	public String getIndice() {
        return indice;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public int getTpsUtilise() {
        return tpsUtilise;
    }

    public void setTpsUtilise(int tpsUtilise) {
        this.tpsUtilise = tpsUtilise;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getTpsBeforeIndice() {
        return tpsBeforeIndice;
    }

    public void setTpsBeforeIndice(int tpsBeforeIndice) {
        this.tpsBeforeIndice = tpsBeforeIndice;
    }

    public int getTpsBeforeSolution() {
        return tpsBeforeSolution;
    }

    public void setTpsBeforeSolution(int tpsBeforeSolution) {
        this.tpsBeforeSolution = tpsBeforeSolution;
    }

    public String getTitreTable() {
        return titreTable;
    }

    public void setTitreTable(String titreTable) {
        this.titreTable = titreTable;
    }
}
