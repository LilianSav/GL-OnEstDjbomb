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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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

    //Player coordinates
    private int coordXPlayer;
    private int coordYPlayer;

    //To read the sprites of the game
    TextureAtlas textureAtlas;
    HashMap<String, Integer> spriteIndexMap;
    Array<Sprite> sprites;


    public EnigmaLabyrinth(boolean isHost, String nameLabyrinth) throws FileNotFoundException {
        super(isHost);

        this.nameLabyrinth=nameLabyrinth;

        setNom("Échappez-vous du labyrinthe!");
        if(isHost){
            setIndice("L'autre équipe est bloquée dans un labyrinthe, vous seul en possédez la carte, " +
                    "guidez-le vers les différents indices pour trouver le code secret permettant de s'enfuir!\n");
        }
        else {
            setTitreTable("Vous êtes bloqué dans un labyrinthe, cliquez sur les cases adjacentes à " +
                    "votre personnage pour vous déplacer et laissez vous guider par votre partenaire pour trouver le code!\n");
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

                tabLabyrinth[i][j] = elem;
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

        textureAtlas = new TextureAtlas(pathLabyrinth+"labyrinthSprites.txt");

        sprites = textureAtlas.createSprites();

        int squareSize = (int)(enigmaManager.getHeight()/ tabLabyrinth.length);

        BitmapFont font = new BitmapFont();

        for (char[] row: tabLabyrinth) {
            for(char elem : row){
                Skin skin = new Skin(textureAtlas);
                skin.getDrawable("LabyrinthStart");

                ImageButton.ImageButtonStyle button = new ImageButton.ImageButtonStyle();
                button.up=skin.getDrawable("LabyrinthWall");

                ImageButton real = new ImageButton(button);
                TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

                enigmaManager.add(real).fill();
            }
            enigmaManager.row();
        }

        /*if(isHost) {
            enigmeImageTexture = enigmeCourante.getTextureTableHost();
        }else {
            enigmeImageTexture = enigmeCourante.getTextureTableGuest();
        }
        Image enigmeImageWidget = new Image(enigmeImageTexture);
        this.add(enigmeImageWidget);*/
    }

    public void dispose(){
        textureAtlas.dispose();
    }

}
