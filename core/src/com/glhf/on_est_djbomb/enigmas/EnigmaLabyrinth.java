package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
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
import com.glhf.on_est_djbomb.dialogs.ClueDialog;
import com.glhf.on_est_djbomb.networking.GameGuestSocket;
import com.glhf.on_est_djbomb.networking.GameHostSocket;
import com.glhf.on_est_djbomb.networking.GameSocket;
import com.glhf.on_est_djbomb.screens.EndGameScreen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

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

    // Shuffled password, used to place the right cells in the labyrinth
    private String shuffledPassword;
    private String passwordIndex;

    //Player coordinates
    private int coordXPlayer;
    private int coordYPlayer;

    //To know if the player asked for a clue
    boolean clueEnabled=false;

    //To read the sprites of the game
    Skin skin;
    TextureAtlas textureAtlas;

    Table fillTable;

    // To communicate with the other computer
    GameSocket gameSocket;


    public EnigmaLabyrinth(boolean isHost, String nameLabyrinth, GameSocket gameSocket) {
        super(isHost);

        this.nameLabyrinth=nameLabyrinth;

        setNom("Échappez-vous du labyrinthe!");

        setTpsBeforeIndice(60);
        setTpsBeforeSolution(100);

        //Read the labyrinth text file to extract information, generates password
        try {
            this.readTextFile(isHost, gameSocket);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(isHost){
            setTitreTable("L'autre équipe est bloquée dans un labyrinthe, vous seul en possédez la carte, guidez-le vers les\n" +
                    " différents indices pour trouver le code secret permettant de s'enfuir!\n");
            setIndice("Oui.");
        }
        else {
            setTitreTable("Vous êtes bloqué dans un labyrinthe, cliquez sur les cases adjacentes à votre personnage pour vous\n " +
                    "déplacer et laissez vous guider par votre partenaire pour trouver le code!\n");
            createDynamicClientMaze();
            setIndice("Vous gardez désormais en mémoire les endroits explorés.");
        }

        // Gestion Message et Game State
        gameSocket.addListener(eventMessage -> {
            // Parse
            String[] tokens = eventMessage.split("::");

            // FLAG TEXT : Pas concerné
            if (tokens[0].equals("TEXT")) {
            }
            // FLAG STATE : Pas concerné
            else if (tokens[0].equals("STATE")) {

            }
            // FLAG LABYRINTH :
            else if (tokens[0].equals("LABYRINTH")) {

            }
            // Flag non reconnu
            else {
                Gdx.app.log("SocketFlagError", "Le flag envoyé par le socket distant n'est pas reconnu");
            }
        });
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
    public void readTextFile(Boolean isHost, GameSocket gameSocket) throws FileNotFoundException {

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

        shuffledPassword="";
        // Put file information into tabLabyrinth, extract password
        shuffledPassword = extractLabyrinth(wordsArray);

        // Find the labyrinth password
        generatePassword(isHost, gameSocket);

        // Create the labyrinth visual
        createDisplayLabyrinth(isHost);
    }

    //Procedure extractLabyrinth, reads the String array parameter and creates the 2D Labyrinth tab
    public String extractLabyrinth(String[] wordsArray){
        //Initialize the password which will be deduced while reading the file
        String shuffledPassword = "";
        Random random = new Random();

        //Loop to read the String[] wordsArray characters one by one
        int i=0;
        int j=0;
        for(String word : wordsArray) {
            for (char elem : word.toCharArray()) {

                // Create the 2D labyrinth and the buttons displayed
                tabLabyrinth[i][j] = elem;

                // Test to add a character to the password when it's a clue for the Host
                if((elem!=WALL) && (elem!= PATH) && (elem !=START) && isHost()){
                    shuffledPassword+=random.nextInt(10);
                }
                j++;
            }
            j = 0;
            i++;
        }
        return shuffledPassword;
    }

    public void generatePassword(boolean isHost, GameSocket gameSocket){
        //Create password index and the password
        String realPass="";
        passwordIndex="";
        if(isHost){ // The host finds the password and sends it

            for(int i = 0 ; i<shuffledPassword.length() ; i++){
                passwordIndex+=i;
            }
            passwordIndex= shuffle(passwordIndex);

            //Send password and index to the Guest
            ((GameHostSocket)gameSocket).sendMessageEnigma(shuffledPassword+":"+passwordIndex);

            realPass=getPassword();
        }
        else { // The guest receives the password

            shuffledPassword=((GameGuestSocket)gameSocket).receiveMessageEnigma();

            String[] received = shuffledPassword.split(":");
            shuffledPassword=received[0];
            passwordIndex=received[1];
            realPass=getPassword();
        }
        //Setting the password
        this.setSolution(Integer.parseInt(realPass));
    }

    public String getPassword(){
        //future password array
        String realPass = "";

        // Placing characters at the right place
        for (int i = 0 ; i < passwordIndex.length() ; i++) {
            realPass+=shuffledPassword.charAt(Character.getNumericValue(passwordIndex.charAt(i)));
        }
        return realPass;
    }

    //Procedure createDisplayLabyrinth, reads the 2D labyrinth tab creates the Labyrinth display
    public void createDisplayLabyrinth(boolean isHost){

        //Initialize the labyrinth sprites
        textureAtlas = new TextureAtlas(pathLabyrinth+"labyrinthSprites.txt");
        skin = new Skin(textureAtlas);

        int count = 0;
        //Loop to read the String[] wordsArray characters one by one
        for(int i = 0 ; i < tabLabyrinth.length ; i++) {
            for (int j = 0; j < tabLabyrinth.length; j++) {

                //Style of the buttons
                ImageButton.ImageButtonStyle imgButtonStyle = new ImageButton.ImageButtonStyle();

                switch (tabLabyrinth[i][j]){
                    case WALL: //if the read character corresponds to a wall
                        imgButtonStyle.up = skin.getDrawable("LabyrinthWall");
                        break;
                    case PATH: //if the read character corresponds to an available path
                        imgButtonStyle.up = skin.getDrawable("LabyrinthPath");
                        break;
                    case START: //if the read character corresponds to the starting place
                        imgButtonStyle.up = skin.getDrawable("LabyrinthStart");
                        coordXPlayer = j;
                        coordYPlayer = i;
                        break;
                    default: //Otherwise, the character corresponds to an element of the password
                        String clue = "LabyrinthClue";
                        if(isHost){
                            clue += passwordIndex.charAt(count);
                        }
                        else {
                            clue += shuffledPassword.charAt(count);
                        }
                        imgButtonStyle.up = skin.getDrawable(clue);
                        count++;
                        break;

                        /*if(isHost){
                            TODO
                            /*String clue = "LabyrinthClue" + String.valueOf(cpt);
                            imgButtonStyle.up = skin.getDrawable(clue);
                            cpt++;
                            password+=elem;
                            password+=random.nextInt(10);
                        }
                        else {
                            TODO

                            String clue = "LabyrinthClue" + elem;
                            imgButtonStyle.up = skin.getDrawable(clue);
                            password+=elem;

                        }*/
                }
                tabButton[j][i] = new ImageButton(imgButtonStyle);

            }
        }

    }

    public void load(boolean isHost, EnigmaManager enigmaManager){

        /*Label titreLabel = new Label(this.getTitreTable(),new Skin(Gdx.files.internal("skincomposerui/skin-composer-ui.json")));
        float heightTitle = enigmaManager.add(titreLabel).getActorHeight();
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
                fillTable.add(elem).width(Value.percentHeight((float)1/(tabLabyrinth.length+3), fillTable)).height(Value.percentHeight((float)1/(tabLabyrinth.length+3), fillTable));
            }
            fillTable.row();
        }
    }

    public void unload(EnigmaManager enigmaManager){
        fillTable.clear();
        enigmaManager.removeActor(fillTable);
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
                                movePlayer((ImageButton) event.getTarget());
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
        //Display player on start cell
        updateDisplayCell(coordXPlayer, coordYPlayer);
    }

    boolean playerAdjacentButton(ImageButton btn){
        return (btn.equals(tabButton[coordXPlayer+1][coordYPlayer])||
                btn.equals(tabButton[coordXPlayer-1][coordYPlayer])||
                btn.equals(tabButton[coordXPlayer][coordYPlayer+1])||
                btn.equals(tabButton[coordXPlayer][coordYPlayer-1]));
    }

    void movePlayer(ImageButton btn){
        for(int relativeX=-1; relativeX<2; relativeX++){
            for(int relativeY=-1; relativeY<2; relativeY++){
                if(btn.equals(tabButton[relativeX+coordXPlayer][relativeY+coordYPlayer])){
                    int newX = relativeX+coordXPlayer;
                    int newY = relativeY+coordYPlayer;
                    updateDisplay(coordXPlayer,coordYPlayer, newX, newY);
                }
            }
        }

    }

    void updateDisplay(int prevCoordX, int prevCoordY, int newCoordX, int newCoordY){
        coordXPlayer=newCoordX;
        coordYPlayer=newCoordY;

        if(!clueEnabled) {
            //Mask old coord maze surroundings
            tabButton[prevCoordX + 1][prevCoordY].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthMask"), null, null, null, null, null));
            tabButton[prevCoordX - 1][prevCoordY].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthMask"), null, null, null, null, null));
            tabButton[prevCoordX][prevCoordY + 1].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthMask"), null, null, null, null, null));
            tabButton[prevCoordX][prevCoordY - 1].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthMask"), null, null, null, null, null));
        }
        //Display new coord maze surroundings
        updateDisplayCell(newCoordX+1, newCoordY);
        updateDisplayCell(newCoordX-1, newCoordY);
        updateDisplayCell(newCoordX, newCoordY+1);
        updateDisplayCell(newCoordX, newCoordY-1);
        updateDisplayCell(coordXPlayer, coordYPlayer);
    }

    public void updateDisplayCell(int coordX, int coordY){
        char cellChar = tabLabyrinth[coordX][coordY];
        if(coordX==coordXPlayer&&coordY==coordYPlayer){
            switch (cellChar){
                case START: //if the read character corresponds to the starting place
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthPlayerStart"),null,null,null,null,null));
                    break;
                default: //Otherwise, the character corresponds to a random path
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthPlayerPath"),null,null,null,null,null));
                    break;
            }
        }
        else{
            switch(cellChar){
                case WALL: //if the read character corresponds to a wall
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthWall"),null,null,null,null,null));
                    break;
                case PATH: //if the read character corresponds to an available path
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthPath"),null,null,null,null,null));
                    break;
                case START: //if the read character corresponds to the starting place
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthStart"),null,null,null,null,null));
                    break;
                default: //Otherwise, the character corresponds to an element of the password
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skin.getDrawable("LabyrinthClue"+tabLabyrinth[coordX][coordY]),null,null,null,null,null));
                    break;
            }
        }
    }

    public String shuffle(String word){
        Random random = new Random();
        // Convert the word into a char array
        char wordArray[] = word.toCharArray();

        // Shuffle the array
        for( int i=0 ; i<wordArray.length ; i++ )
        {
            int j = random.nextInt(wordArray.length);
            // Swap letters
            char chatTemp = wordArray[i];
            wordArray[i] = wordArray[j];
            wordArray[j] = chatTemp;
        }

        return new String( wordArray );
    }

    public void chargerIndice(){
        clueEnabled=true;
    }

    public void dispose(){
        textureAtlas.dispose();

    }

}
