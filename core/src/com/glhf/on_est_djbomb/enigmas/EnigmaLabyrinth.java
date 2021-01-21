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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    //2D array of Buttons to display during the enigma
    private ImageButton[][] tabButton;

    //Player coordinates
    private int coordXPlayer;
    private int coordYPlayer;

    //To read the sprites of the game
    Skin skin;
    TextureAtlas textureAtlas;

    Table fillTable;


    public EnigmaLabyrinth(boolean isHost, String nameLabyrinth) throws FileNotFoundException {
        super(isHost);

        this.nameLabyrinth=nameLabyrinth;

        setNom("Échappez-vous du labyrinthe!");

        setTpsBeforeIndice(60);
        setTpsBeforeSolution(100);

        //Read the labyrinth text file to extract information
        this.readTextFile(isHost);

        if(isHost){
            setTitreTable("L'autre équipe est bloquée dans un labyrinthe, vous seul en possédez la carte, guidez-le vers les\n" +
                    " différents indices pour trouver le code secret permettant de s'enfuir!\n");
        }
        else {
            setTitreTable("Vous êtes bloqué dans un labyrinthe, cliquez sur les cases adjacentes à votre personnage pour vous\n " +
                    "déplacer et laissez vous guider par votre partenaire pour trouver le code!\n");
            //createDynamicClientMaze();
        }
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
    public void readTextFile(Boolean isHost) throws FileNotFoundException {

        //Read the desired file
        FileHandle handle = Gdx.files.internal(pathLabyrinth+nameLabyrinth);
        String text = handle.readString();

        //Isolate each line of the file
        String wordsArray[] = text.split("\\r?\\n");

        //Get the dimensions of the labyrinth, the horizontal and vertical lengths have to be the same
        int labLength = wordsArray.length;

        //Initialize the labyrinth arrays
        tabLabyrinth = new char[labLength][labLength];
        tabButton = new ImageButton[labLength][labLength];

        //Put file information into tabLabyrinth
        extractLabyrinth(wordsArray, isHost);
    }

    //Procedure extractLabyrinth, reads the String array parameter and creates the Labyrinth
    public void extractLabyrinth(String[] wordsArray, boolean isHost){
        //Initialize the password which will be deduced while reading the file
        String password = "";

        //Initialize the labyrinth sprites
        textureAtlas = new TextureAtlas(pathLabyrinth+"labyrinthSprites.txt");
        skin = new Skin(textureAtlas);

        //cpt, to know the index of the clue associated to the password number
        int cpt=1;

        //Loop to read the String[] wordsArray characters one by one
        int i=0;
        int j=0;
        for(String word : wordsArray) {
            for (char elem : word.toCharArray()) {

                //Create the 2D labyrinth and the buttons displayed
                tabLabyrinth[j][i] = elem;
                ImageButton.ImageButtonStyle imgButtonStyle = new ImageButton.ImageButtonStyle();

                switch (elem){
                    case WALL: //if the read character corresponds to a wall
                        imgButtonStyle.up = skin.getDrawable("LabyrinthWall");
                        break;
                    case PATH: //if the read character corresponds to an available path
                        imgButtonStyle.up = skin.getDrawable("LabyrinthPath");
                        break;
                    case START: //if the read character corresponds to the starting place
                        imgButtonStyle.up = skin.getDrawable("LabyrinthStart");
                        coordXPlayer = i;
                        coordYPlayer = j;
                        break;
                    default: //Otherwise, the character corresponds to an element of the password
                        if(isHost){
                            String clue = "LabyrinthClue" + String.valueOf(cpt);
                            imgButtonStyle.up = skin.getDrawable(clue);
                            cpt++;
                            password+=elem;
                        }
                        else {
                            String clue = "LabyrinthClue" + elem;
                            imgButtonStyle.up = skin.getDrawable(clue);
                            password+=elem;
                        }
                        break;
                }
                tabButton[j][i] = new ImageButton(imgButtonStyle);

                i++;
            }
            i = 0;
            j++;
        }
        //Setting the password
        this.setSolution(Integer.parseInt(password));
    }

    public void load(boolean isHost, EnigmaManager enigmaManager){
        if (isHost){
            loadHost(isHost,  enigmaManager);
        }
        else {
            loadHost(isHost,  enigmaManager);
        }
    }
    public void loadHost(boolean isHost, EnigmaManager enigmaManager){

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

        int squareSize = (int)(fillTable.getHeight()/ tabLabyrinth.length);


        for (ImageButton[] row: tabButton) {
            for(Button elem : row){

                //ImageButton real = new ImageButton(button);

                fillTable.add(elem).width(Value.percentHeight((float)1/(tabLabyrinth.length+1), fillTable)).height(Value.percentHeight((float)1/(tabLabyrinth.length+1), fillTable));
            }
            fillTable.row();
        }
    }

    public void loadClient(boolean isHost, EnigmaManager enigmaManager){

    }

    public void createDynamicClientMaze(){

        //Add event Handlers
        for (int i = 0; i< tabLabyrinth.length; i++) {
            for (int j = 0; j<tabLabyrinth.length; j++){
                if(tabLabyrinth[i][j]!=WALL){
                    tabButton[i][j].addListener(new ClickListener() {

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if(playerAdjacentButton((ImageButton) event.getTarget())){
                                System.out.println("trouvé");
                            }
                        };
                    });
                }
            }

        }

        //Mask maze
        for (ImageButton[]imgBtnTable:tabButton) {
            for (ImageButton imgBtn: imgBtnTable){
                if(!playerAdjacentButton(imgBtn)&&!(imgBtn.equals(tabButton[coordXPlayer][coordYPlayer]))){

                    imgBtn.setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthMask"),null,null,null,null,null));
                }
            }
        }

    }

    boolean playerAdjacentButton(ImageButton btn){
        return (btn.equals(tabButton[coordXPlayer+1][coordYPlayer])||
                btn.equals(tabButton[coordXPlayer-1][coordYPlayer])||
                btn.equals(tabButton[coordXPlayer][coordYPlayer+1])||
                btn.equals(tabButton[coordXPlayer][coordYPlayer-1]));
    }

    public void dispose(){
        textureAtlas.dispose();

    }

}
