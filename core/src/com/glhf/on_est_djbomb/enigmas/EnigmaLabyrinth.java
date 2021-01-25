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
    private String shuffledPassword;
    private String password;
    private String passwordIndex;
    private Object lock;
    private boolean extracted=false;

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
        lock = new Object();

    }

    // Procedure readTextFile reads the labyrinth text file and extracts information
    public synchronized void readTextFile() throws FileNotFoundException, InterruptedException {

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

        if(isHost()){
            // Find the labyrinth password
            generatePassword();
        }
        // Create the labyrinth visual
        createDisplayLabyrinth();

        if(!isHost()){
            extracted=true;
        }
    }

    public void getPassword(){

        String realPass = "";

        // Placing characters at the right place
        for (int i = 0 ; i < passwordIndex.length() ; i++) {
            realPass+=shuffledPassword.charAt(Character.getNumericValue(passwordIndex.charAt(i)));
        }
        password=realPass;
    }

    //Procedure extractLabyrinth, reads the String array parameter and creates the Labyrinth
    public void extractLabyrinth(String[] wordsArray) {
        //Initialize the password which will be deduced while reading the file
        shuffledPassword = "";
        Random random = new Random();

        //Loop to read the String[] wordsArray characters one by one
        int i=0;
        int j=0;
        for(String word : wordsArray) {
            for (char elem : word.toCharArray()) {

                // Create the 2D labyrinth and the buttons displayed
                tabLabyrinth[i][j] = elem;

                // Test to add a character to the password when it's a clue for the Host
                if((elem!=WALL) && (elem!= PATH) && (elem !=START)){
                    int val =random.nextInt(10);
                    shuffledPassword+=val;
                    tabLabyrinth[i][j] = String.valueOf(val).charAt(0);
                }
                j++;
            }
            j = 0;
            i++;
        }
    }

    public void generatePassword(){
        password="";
        passwordIndex="";

        // Initialisation de passwordIndex
        for(int i = 0 ; i<shuffledPassword.length() ; i++){
            passwordIndex+=i;
        }

        passwordIndex= shuffle(passwordIndex); // Randomisation
        getPassword();

        this.setSolution(Integer.parseInt(password));

        socket.sendMessage("LABYRINTH::"+numero+"::"+"CODE:"+password+":"+passwordIndex+":"+shuffledPassword);
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
                        coordXPlayer = j;
                        coordYPlayer = i;
                        break;
                    default: //Otherwise, the character corresponds to an element of the password
                        String clue = "LabyrinthClue";
                        if (this.isHost()) {
                            clue += String.valueOf(Character.getNumericValue(passwordIndex.charAt(count))+1);
                        }
                        else {
                            clue +=shuffledPassword.charAt(count);
                        }
                        imgButtonStyle.up = skinLabyrinthe.getDrawable(clue);
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

        if(!isHost()){
            // On délègue la gestion du password à un Thread
            new Thread(() -> {
                try {
                    synchronized (lock) {
                        lock.wait();
                        while(!extracted){
                            //wait
                        }
                        updateMazePassword();
                    }
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
                case PATH: //if the read character corresponds to an available path
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthPath"), null, null, null, null, null));
                    break;
                case START: //if the read character corresponds to the starting place
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthStart"), null, null, null, null, null));
                    break;
                default: //Otherwise, the character corresponds to an element of the password
                    for(char[] elem:tabLabyrinth){
                        for(char e : elem){
                            System.out.print(e+" ");
                        }
                        System.out.println();
                    }
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthClue" + tabLabyrinth[coordX][coordY]), null, null, null, null, null));
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
            // Setting the password
            this.setSolution(Integer.parseInt(password));
            synchronized (lock){
                lock.notify();
            }
        }
    }

    public void dispose() {
        textureAtlas.dispose();
    }

}
