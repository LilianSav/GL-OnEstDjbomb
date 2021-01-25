package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.glhf.on_est_djbomb.networking.GameSocket;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class EnigmaLabyrinth extends EnigmaSkeleton {

    //Labyrinth text file known elements
    private final static char WALL = '*';
    private final static char PATH = ' ';
    private final static char START = 'X';
    private final static char CLUE = '?';

    private final static int VISION = 1;
    private final static int VISION_HELP = 2;

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

    //To know if the player asked for a clue
    boolean clueEnabled = false;

    // Attributs permettant la communication
    private int numero;
    private GameSocket socket;
    private String shuffledPassword="";
    private String password="";
    private String passwordIndex="";
    private Object lock_com;

    // Verrou permettant de synchroniser le mot de passe du client
    private Object lock_algo;

    //To read the sprites of the game
    Skin skinLabyrinthe;
    TextureAtlas textureAtlas;

    Table fillTable;

    public EnigmaLabyrinth(boolean isHost, String nameLabyrinth, int numero, GameSocket socket) {
        super(isHost);

        this.nameLabyrinth = nameLabyrinth;
        this.numero = numero;
        this.socket = socket;

        setNom("Échappez-vous du labyrinthe!");

        setTpsBeforeIndice(10);
        setTpsBeforeSolution(100);

        if (isHost) {
            setTitreTable("L'autre équipe est bloquée dans un labyrinthe, vous seul en possédez la carte, guidez-le vers les\n" +
                    " différents indices pour trouver le code secret permettant de s'enfuir!\n");
            setIndice("Dites à vos partenaires de demander un indice");
        } else {
            setTitreTable("Vous êtes bloqué dans un labyrinthe, cliquez sur les cases adjacentes à votre personnage pour vous\n " +
                    "déplacer et laissez vous guider par votre partenaire pour trouver le code!\n");
            setIndice("Vous êtes désormais plus clairvoyant et avez amélioré votre mémoire");
        }

        // Verrou de communication
        lock_com = new Object();
        lock_algo = new Object();

        if(!isHost()){
            // On délègue la gestion du password à un Thread
            new Thread(() -> {
                try {
                    synchronized (lock_com) {
                        lock_com.wait();
                    }
                    System.out.println("1 : "+shuffledPassword);
                    System.out.println(shuffledPassword);
                    synchronized (lock_algo){
                        lock_algo.wait();
                    }
                    System.out.println("2 : "+shuffledPassword);
                    System.out.println("VERROUS PASSÉS : \nLABYRINTH::"+numero+"::"+"CODE:"+password+":"+passwordIndex+":"+shuffledPassword+");");
                    updateMazePassword();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        //Read the labyrinth text file to extract information
        try {
            this.readTextFile();
        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    // Procedure readTextFile reads the labyrinth text file and extracts information
    public void readTextFile() throws FileNotFoundException, InterruptedException {

        //Read the desired file
        FileHandle handle = Gdx.files.internal(pathLabyrinth + nameLabyrinth);
        String text = handle.readString();

        //Isolate each line of the file
        String wordsArray[] = text.split("\\r?\\n");

        //Get the dimensions of the labyrinth, the horizontal and vertical lengths have to be the same
        int labLength = wordsArray.length;

        //Initialize the labyrinth arrays
        tabLabyrinth = new char[labLength][labLength];
        tabButton = new ImageButton[labLength][labLength];

        //Put file information into tabLabyrinth
        extractLabyrinth(wordsArray);

        if (isHost()) {
            // Find the labyrinth password
            generatePassword();
        }
    }

    public void getPassword(){

        String realPass = "";

        // Placing characters at the right place
        for (int i = 0 ; i < passwordIndex.length() ; i++) {
            realPass+= shuffledPassword.charAt(Character.getNumericValue(passwordIndex.charAt(i)));
        }
        password=realPass;
    }

    //Procedure extractLabyrinth, reads the String array parameter and creates the Labyrinth
    public void extractLabyrinth(String[] wordsArray) {
        //Initialize the password which will be deduced while reading the file
        Random random = new Random();

        //Loop to read the String[] wordsArray characters one by one
        for(int i=0 ; i<wordsArray.length ; i++){
            for(int j=0 ; j<wordsArray.length ; j++) {

                char elem = wordsArray[i].charAt(j);
                // Create the 2D labyrinth and the buttons displayed
                tabLabyrinth[i][j] = elem;

                // Test to add a character to the password when it's a clue for the Host
                if((elem!=WALL) && (elem!= PATH) && (elem !=START)){
                    if(isHost()) {
                        int val = random.nextInt(9)+1;
                        shuffledPassword += val;
                        tabLabyrinth[i][j] = String.valueOf(val).charAt(0);
                    }
                }
            }
        }
    }

    public void generatePassword(){

        // Initialisation de passwordIndex
        for(int i = 0 ; i<shuffledPassword.length() ; i++){
            passwordIndex+=i;
        }

        passwordIndex= shuffle(passwordIndex); // Randomisation

        getPassword();

        this.setSolution(Integer.parseInt(password));

        shuffledPassword="";
        for(int i=0 ; i< password.length() ; i++){
            shuffledPassword += String.valueOf(Character.getNumericValue(password.charAt(Character.getNumericValue(passwordIndex.charAt(i)))));
        }

    }

        //Procedure createDisplayLabyrinth, reads the 2D labyrinth tab creates the Labyrinth display
        public void createDisplayLabyrinth(){
        //Initialize the labyrinth sprites
        textureAtlas = new TextureAtlas(pathLabyrinth + "labyrinthSprites.txt");
        skinLabyrinthe = new Skin(textureAtlas);

        int count=0;
        //Loop to read the String[] wordsArray characters one by one
        for(int i = 0 ; i < tabLabyrinth.length ; i++) {
            for (int j = 0; j < tabLabyrinth.length; j++) {

                //Style of the buttons
                ImageButton.ImageButtonStyle imgButtonStyle = new ImageButton.ImageButtonStyle();

                switch (tabLabyrinth[i][j]){
                    case WALL: //if the read character corresponds to a wall
                        imgButtonStyle.up = skinLabyrinthe.getDrawable("LabyrinthWall");
                        break;
                    case PATH: //if the read character corresponds to an available path
                        imgButtonStyle.up = skinLabyrinthe.getDrawable("LabyrinthPath");
                        break;
                    case START: //if the read character corresponds to the starting place
                        imgButtonStyle.up = skinLabyrinthe.getDrawable("LabyrinthStart");
                        coordXPlayer = i;
                        coordYPlayer = j;
                        break;
                    default: //Otherwise, the character corresponds to an element of the password
                        String display = "Labyrinth";
                        if (this.isHost()) {
                            display += "Clue"+String.valueOf(Character.getNumericValue(passwordIndex.charAt(count))+1);
                        }
                        else {
                            if(shuffledPassword.equals("")){
                                display += "Path";
                            }
                            else {
                                display += "Clue" + shuffledPassword.charAt(count);
                            }

                        }
                        imgButtonStyle.up = skinLabyrinthe.getDrawable(display);
                        count++;
                        break;
                }
                tabButton[i][j] = new ImageButton(imgButtonStyle);
            }
        }
    }

    public void updateMazePassword() {
        int count = 0;
        //Loop to read the String[] wordsArray characters one by one
        for (int i = 0; i < tabLabyrinth.length; i++) {
            for (int j = 0; j < tabLabyrinth.length; j++) {
                switch (tabLabyrinth[i][j]){
                    case WALL: // if the read character corresponds to a wall
                        break;
                    case PATH: // if the read character corresponds to an available path
                        break;
                    case START: // if the read character corresponds to the starting place
                        break;
                    default: // Otherwise, the character corresponds to an element of the password
                        char val = shuffledPassword.charAt(count);
                        tabLabyrinth[i][j]=val;
                        String clue = "LabyrinthClue"+val;
                        if(playerAdjacentButton(tabButton[i][j])) {
                            tabButton[i][j].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable(clue), null, null, null, null, null));
                        }
                        else {
                            tabButton[i][j].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthMask"), null, null, null, null, null));
                        }
                        count++;
                        break;
                    }
                }
            }
        }

    public void load( Table enigmaManager) {
        System.out.println(this.isHost());

        if(this.isHost()){ // On envoie le password au Client
            socket.sendMessage("LABYRINTH::"+numero+"::"+"CODE:"+password+":"+passwordIndex+":"+shuffledPassword);
            System.out.println("socket.sendMessage(\nLABYRINTH::"+numero+"::"+"CODE:"+password+":"+passwordIndex+":"+shuffledPassword+");");
        }

        // Create the labyrinth visual
        createDisplayLabyrinth();

        if(!this.isHost()){
            createDynamicClientMaze();
        }

        fillTable = new Table();
        fillTable.setFillParent(true);

        enigmaManager.add(fillTable).grow();

        //Creating a square
        fillTable.setHeight(enigmaManager.getHeight());
        fillTable.setWidth(enigmaManager.getHeight());

        for (ImageButton[] row : tabButton) {
            for (Button elem : row) {
                fillTable.add(elem).width(Value.percentHeight((float) 1 / (tabLabyrinth.length + 3), fillTable)).height(Value.percentHeight((float) 1 / (tabLabyrinth.length + 3), fillTable));
            }
            fillTable.row();
        }

        if(!isHost()){
            synchronized (lock_algo){
                lock_algo.notify();
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
        return new String(wordArray );
    }


    public void unload() {
        skinLabyrinthe.dispose();
        textureAtlas.dispose();
    }

    public void createDynamicClientMaze() {
        //Add event Handlers
        for (int i = 0; i < tabLabyrinth.length; i++) {
            for (int j = 0; j < tabLabyrinth.length; j++) {
                if (tabLabyrinth[i][j] != WALL) {
                    tabButton[i][j].addListener(new ClickListener() {

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (playerAdjacentButton((ImageButton) event.getTarget())) {
                                movePlayer((ImageButton) event.getTarget());
                            }
                        }
                    });
                }

            }

            //Mask maze
            for (ImageButton[] imgBtnTable : tabButton) {
                for (ImageButton imgBtn : imgBtnTable) {
                    if (!playerAdjacentButton(imgBtn) && !(imgBtn.equals(tabButton[coordXPlayer][coordYPlayer]))) {
                        imgBtn.setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthMask"), null, null, null, null, null));
                    }
                }
            }
            //Display player on start cell
            updateDisplayCell(coordXPlayer, coordYPlayer);
        }
    }

    boolean playerAdjacentButton(ImageButton btn) {
        return (btn.equals(tabButton[coordXPlayer + 1][coordYPlayer]) ||
                btn.equals(tabButton[coordXPlayer - 1][coordYPlayer]) ||
                btn.equals(tabButton[coordXPlayer][coordYPlayer + 1]) ||
                btn.equals(tabButton[coordXPlayer][coordYPlayer - 1]));
    }

    void movePlayer(ImageButton btn) {
        int newX = 0;
        int newY = 0;
        for (int relativeX = -1; relativeX < 2; relativeX++) {
            for (int relativeY = -1; relativeY < 2; relativeY++) {
                if (btn.equals(tabButton[relativeX + coordXPlayer][relativeY + coordYPlayer])) {
                    newX = relativeX + coordXPlayer;
                    newY = relativeY + coordYPlayer;
                }
            }
        }
        updateDisplay(coordXPlayer, coordYPlayer, newX, newY);

    }

    void updateDisplay(int prevCoordX, int prevCoordY, int newCoordX, int newCoordY) {
        //Maj coordonnées
        coordXPlayer = newCoordX;
        coordYPlayer = newCoordY;

        if (!clueEnabled) {
            //Mask old coord maze surroundings
            for (int i = -VISION; i <= VISION; i++) {
                for (int j = -VISION; j <= VISION; j++) {
                    if ((prevCoordX + i >= 0) && (prevCoordX + i < tabLabyrinth.length) && (prevCoordY + j >= 0) && (prevCoordY + j < tabLabyrinth.length)) {
                        tabButton[prevCoordX + i][prevCoordY + j].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthMask"), null, null, null, null, null));
                    }
                }
            }
            //Display new coord maze surroundings
            displaySurroundings(newCoordX, newCoordY, VISION);
        } else {
            //Display new coord maze surroundings
            displaySurroundings(newCoordX, newCoordY, VISION_HELP);
        }
    }

    public void displaySurroundings(int coordX, int coordY, int vision) {
        for (int i = -vision; i <= vision; i++) {
            for (int j = -vision; j <= vision; j++) {
                if ((coordX + i >= 0) && (coordX + i < tabLabyrinth.length) && (coordY + j >= 0) && (coordY + j < tabLabyrinth.length) && (Math.abs(i + j) <= vision) && (Math.abs(i - j) <= vision)) {
                    updateDisplayCell(coordX + i, coordY + j);
                }
            }
        }

    }

    public void updateDisplayCell(int coordX, int coordY) {
            char cellChar = tabLabyrinth[coordX][coordY];
            if (coordX == coordXPlayer && coordY == coordYPlayer) {
                switch (cellChar) {
                    case START: //if the read character corresponds to the starting place
                        tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthPlayerStart"), null, null, null, null, null));
                        break;
                    default: //Otherwise, the character corresponds to a random path
                        tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthPlayerPath"), null, null, null, null, null));
                        break;
                }
            } else {
                switch (cellChar) {
                    case WALL: //if the read character corresponds to a wall
                        tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthWall"), null, null, null, null, null));
                        break;
                    case PATH: // if the read character corresponds to an available path
                        tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthPath"), null, null, null, null, null));
                        break;
                    case START: //if the read character corresponds to the starting place
                        tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthStart"), null, null, null, null, null));
                        break;
                    case CLUE: // Symbol for clue still not updated
                        if (!shuffledPassword.equals("") ) {
                            updateMazePassword();
                        }
                    default: //Otherwise, the character corresponds to an element of the password
                        if (shuffledPassword.equals("") ) {
                            tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthPath"), null, null, null, null, null));
                        } else {
                            System.out.println("shuffledPassword : " + shuffledPassword);
                            tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthClue" + tabLabyrinth[coordX][coordY]), null, null, null, null, null));
                        }
                        break;
                }
            }
    }

    public void chargerIndice() {
        clueEnabled = true;
        displaySurroundings(coordXPlayer, coordYPlayer, VISION_HELP);
    }

    public void traiterMessage(String msg){
        System.out.println("Message reçu : "+msg);
        String[] tokens = msg.split(":");
        // FLAG CODE
        if (tokens[0].equals("CODE")) {
            password=tokens[1];
            passwordIndex=tokens[2];
            shuffledPassword=tokens[3];
            System.out.println("token[3] : "+tokens[3]);
            System.out.println("shuffledPassword : "+shuffledPassword);
            // Setting the password
            this.setSolution(Integer.parseInt(password));
            System.out.println(Integer.parseInt(password));
            System.out.println(this.getSolution());
            synchronized (lock_com){
                lock_com.notify();
            }
        }
    }

    public void dispose() {
        textureAtlas.dispose();
    }

}
