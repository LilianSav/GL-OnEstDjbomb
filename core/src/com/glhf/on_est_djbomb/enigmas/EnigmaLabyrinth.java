package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class EnigmaLabyrinth extends EnigmaSkeleton{

    //Labyrinth text file known elements
    private final static char WALL = '*';
    private final static char PATH = ' ';
    private final static char START = 'X';

    //Available files path
    private String pathLabyrinth = "assetEnigme/labyrinthe/";
    private String nameLabyrinth;

    //2D array, corresponds to the given text file
    private char[][] tabLabyrinth;

    //2D array of Buttons, corresponds to tabLabyrinth
    private Button[][] tabButton;

    //Player coordinates
    private int coordXPlayer;
    private int coordYPlayer;

    //To read the sprites of the game
    TextureAtlas textureAtlas;

    Array<Sprite> sprites;

    Table fillTable;


    public EnigmaLabyrinth(boolean isHost, String nameLabyrinth) throws FileNotFoundException {
        super(isHost);

        this.nameLabyrinth=nameLabyrinth;

        setNom("Échappez-vous du labyrinthe!");
        if(isHost){
            //setTitreTable("");
            setTitreTable("L'autre équipe est bloquée dans un labyrinthe, vous seul en possédez la carte, guidez-le vers les\n" +
                    " différents indices pour trouver le code secret permettant de s'enfuir!\n");
        }
        else {
            //setTitreTable("");
            setTitreTable("Vous êtes bloqué dans un labyrinthe, cliquez sur les cases adjacentes à votre personnage pour vous\n " +
                    "déplacer et laissez vous guider par votre partenaire pour trouver le code!\n");
        }

        setTpsBeforeIndice(60);
        setTpsBeforeSolution(100);

        //Read the labyrinth text file to extract information
        this.readTextFile();
    }

    @Override
    public Texture getTextureTableHost() {
        return null;
    }

    @Override
    public Texture getTextureTableGuest() {
        return null;
    }

    // Procedure readTextFile reads the labyrinth text file and extracts information
    public void readTextFile() throws FileNotFoundException {

        //Read the desired file
        FileHandle handle = Gdx.files.internal(pathLabyrinth+nameLabyrinth);
        String text = handle.readString();

        //Isolate each line of the file
        String wordsArray[] = text.split("\\r?\\n");

        //Get the dimensions of the labyrinth, the horizontal and vertical lengths have to be the same
        int labLength = wordsArray.length;
        tabLabyrinth = new char[labLength][labLength];

        //Initialize the password which will be deduced while reading the file
        String password = "";

        //Put file information into tabLabyrinth
        int i=0;
        int j=0;
        for(String word : wordsArray) {

            for (char elem : word.toCharArray()) {

                tabLabyrinth[j][i] = elem;
                switch (elem){
                    case WALL: //if the read character corresponds to a wall
                        break;
                    case PATH: //if the read character corresponds to an available path
                        break;
                    case START: //if the read character corresponds to the starting place
                        coordXPlayer = i;
                        coordYPlayer = j;
                        break;
                    default: //Otherwise, the character corresponds to an element of the password
                        password+=elem;
                        break;
                }
                i++;
            }
            i = 0;
            j++;
        }

        //Setting the password
        this.setSolution(Integer.parseInt(password));
    }

    public void load(boolean isHost, EnigmaManager enigmaManager){



        /*//Place title
        Label titreLabel = new Label(this.getTitreTable(),new Skin(Gdx.files.internal("skincomposerui/skin-composer-ui.json")));
        enigmaManager.add(titreLabel).growX().pad(50f);
        enigmaManager.row();*/

        fillTable=new Table();
        fillTable.setFillParent(true);

        //enigmaManager.add(fillTable).width(Value.percentHeight(0.100f, enigmaManager)).height(Value.percentHeight(0.100f, enigmaManager)).grow();

        enigmaManager.add(fillTable).grow();


        //Creating a square
        fillTable.setHeight(enigmaManager.getHeight());
        fillTable.setWidth(enigmaManager.getHeight());

        textureAtlas = new TextureAtlas(pathLabyrinth+"labyrinthSprites.txt");

        sprites = textureAtlas.createSprites();

        int squareSize = (int)(fillTable.getHeight()/ tabLabyrinth.length);

        int cpt=1;
        for (char[] row: tabLabyrinth) {
            for(char elem : row){
                Skin skin = new Skin(textureAtlas);

                ImageButton.ImageButtonStyle button = new ImageButton.ImageButtonStyle();

                switch (elem) {
                    case WALL: //if the read character corresponds to a wall
                        button.up = skin.getDrawable("LabyrinthWall");
                        break;
                    case PATH: //if the read character corresponds to an available path
                        button.up = skin.getDrawable("LabyrinthPath");
                        break;
                    case START: //if the read character corresponds to the starting place
                        button.up = skin.getDrawable("LabyrinthStart");
                        break;
                    default: //Otherwise, the character corresponds to an element of the password
                        String clue = "LabyrinthClue" + String.valueOf(cpt);
                        button.up = skin.getDrawable(clue);
                        cpt++;
                        break;
                }

                ImageButton real = new ImageButton(button);
                TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

                fillTable.add(real).width(Value.percentHeight((float)1/(tabLabyrinth.length+1), fillTable)).height(Value.percentHeight((float)1/(tabLabyrinth.length+1), fillTable));
            }
            fillTable.row();
        }
    }
/*
    public void loadClient(boolean isHost, EnigmaManager enigmaManager){

        textureAtlas = new TextureAtlas(pathLabyrinth+"labyrinthSprites.txt");

        sprites = textureAtlas.createSprites();

        int squareSize = (int)(enigmaManager.getHeight()/ tabLabyrinth.length);

        BitmapFont font = new BitmapFont();

        int cpt=1;
        for (char[] row: tabLabyrinth) {
            for(char elem : row){
                Skin skin = new Skin(textureAtlas);

                ImageButton.ImageButtonStyle button = new ImageButton.ImageButtonStyle();

                switch (elem){
                    case WALL: //if the read character corresponds to a wall
                        button.up=skin.getDrawable("LabyrinthWall");
                        break;
                    case PATH: //if the read character corresponds to an available path
                        button.up=skin.getDrawable("LabyrinthPath");
                        break;
                    case START: //if the read character corresponds to the starting place
                        button.up=skin.getDrawable("LabyrinthStart");
                        break;
                    default: //Otherwise, the character corresponds to an element of the password
                        String clue = "LabyrinthClue"+String.valueOf(cpt);
                        System.out.println(clue);
                        button.up=skin.getDrawable(clue);
                        cpt++;
                        break;
                }

                ImageButton real = new ImageButton(button);
                TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

                enigmaManager.add(real);
            }
            enigmaManager.row();
        }
    }*/

    public void dispose(){
        textureAtlas.dispose();

    }

}
