package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.glhf.on_est_djbomb.networking.GameSocket;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class EnigmaLabyrinth extends EnigmaSkeleton {

    //Labyrinth text file known elements
    private final static char WALL = '*';
    private final static char PATH = '_';
    private final static char START = 'X';
    private final static char CLUE = '?';

    // Options
    private final static int VISION = 1; // Distance vue par le joueur Client sans indice (échelle : longueur d'une cellule)
    private final static boolean MEMORY_NOCLUE = false; // Mémoire du joueur Client sans indice
    private final static int VISION_HELP = 2; // Distance vue par le joueur Client avec indice
    private final static boolean MEMORY_CLUE = true; // Mémoire du joueur Client avec indice
    private final static boolean DISPLAY_ON_HOST_PRECLUE = false; // Activer les déplacements sur l'hôte sans l'indice

    //Available files path
    private final static String pathLabyrinth = "assetEnigme/labyrinthe/";
    private String nameLabyrinth;

    //2D array, corresponds to the given text file
    private char[][] tabLabyrinth;

    //2D array of Buttons to display during the enigma
    private ImageButton[][] tabButton;

    //Player coordinates
    private int coordXPlayer;
    private int coordYPlayer;

    //Received coordinates
    private int receivedX;
    private int receivedY;

    // Attributs permettant la communication
    private int numero;
    private GameSocket socket;
    private String shuffledPassword="";
    private String password="";
    private String passwordIndex="";

    // true if the player asked for help
    boolean clueEnabled = false;

    // Verrou permettant de synchroniser le mot de passe du client
    private final ArrayBlockingQueue<Object> lock_algo_queue;
    private final ArrayBlockingQueue<Object> lock_com_queue;
    boolean lockFreed;

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
            setTitreTable("Aidez vos partenaires à s'échapper !");
            setIndice("Vous pouvez désormais voir les déplacements");
        } else {
            setTitreTable("Echappez-vous !");
            setIndice("Vous avez désormais une meilleure visibilité et avez amélioré votre mémoire");
        }

        // Verrou de communication
        lock_algo_queue = new ArrayBlockingQueue<>(2);
        lock_com_queue = new ArrayBlockingQueue<>(2);
        lockFreed = false;

        if(!isHost()){
            // On délègue la gestion du password à un Thread
            new Thread(() -> {
                try {
                    lock_com_queue.take();
                    lock_algo_queue.take();
                    if(!lockFreed){
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
        int val = numero+1;
        System.out.println("Labyrinthe "+val+" password : "+password);

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
                        receivedX=i;
                        receivedY=j;
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
                            int distanceX=coordXPlayer-i;
                            int distanceY=coordYPlayer-j;
                            double distance = Math.pow(distanceX,2)+Math.pow(distanceY,2); // Formule rayon d'un cercle
                            if (Math.sqrt(distance) <= VISION){ // Le joueur a la vision
                                tabButton[i][j].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable(clue), null, null, null, null, null));
                            }
                            else {
                                tabButton[i][j].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthMask"), null, null, null, null, null));
                            }
                        }
                        count++;
                        break;
                    }
                }
            }
        }

    public void load( Table enigmaManager) {
        // Chargement du titre
        enigmaManager.add(new Label(getTitreTable(), enigmaManager.getSkin(), "title")).pad(5);
        enigmaManager.row();


        if(this.isHost()){ // On envoie le password au Client
            socket.sendMessage("LABYRINTH::"+numero+"::"+"CODE:"+password+":"+passwordIndex+":"+shuffledPassword);
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
            try {
                lock_algo_queue.put(new Object());
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        }
        // Mask maze
        for( int i=0 ; i<tabLabyrinth.length ; i++){
            for( int j=0 ; j<tabLabyrinth.length ; j++){
                int distanceX=coordXPlayer-i;
                int distanceY=coordYPlayer-j;
                double val = Math.pow(distanceX,2)+Math.pow(distanceY,2);
                if (Math.sqrt(val) > VISION){ // Hors de la vue du joueur
                    tabButton[i][j].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthMask"), null, null, null, null, null));
                }
            }
        }
        //Display player on start cell
        updateDisplayCell(coordXPlayer, coordYPlayer);
    }


    boolean playerAdjacentButton(ImageButton btn) {

        boolean checkNorth = false;
        boolean checkSouth = false;
        boolean checkWest = false;
        boolean checkEast = false;

        if(coordYPlayer-1>=0){ // North
            checkNorth= btn.equals(tabButton[coordXPlayer][coordYPlayer - 1]);
        }
        if(coordYPlayer+1<tabLabyrinth.length){ // South
            checkSouth = btn.equals(tabButton[coordXPlayer][coordYPlayer + 1]);
        }
        if(coordXPlayer-1>=0){ // West
            checkWest = btn.equals(tabButton[coordXPlayer - 1][coordYPlayer]);
        }
        if(coordXPlayer+1<tabLabyrinth.length){ // East
            checkEast = btn.equals(tabButton[coordXPlayer + 1][coordYPlayer]);
        }

        return (checkNorth || checkSouth || checkWest ||checkEast);
    }

    void movePlayer(ImageButton btn) {
        int newX = 0;
        int newY = 0;
        for (int relativeX = -1; relativeX < 2; relativeX++) {
            for (int relativeY = -1; relativeY < 2; relativeY++) {
                if ((coordXPlayer + relativeX >= 0) && (coordXPlayer + relativeX < tabLabyrinth.length) && (coordYPlayer + relativeY >= 0) && (coordYPlayer + relativeY < tabLabyrinth.length)) {
                    if (btn.equals(tabButton[relativeX + coordXPlayer][relativeY + coordYPlayer])) {
                        newX = relativeX + coordXPlayer;
                        newY = relativeY + coordYPlayer;
                        socket.sendMessage("LABYRINTH::"+numero+"::"+"MOVEMENT:"+newX+":"+newY);
                    }
                }
            }
        }
        updateDisplay(coordXPlayer, coordYPlayer, newX, newY);
    }

    void updateDisplay(int prevCoordX, int prevCoordY, int newCoordX, int newCoordY) {
        //Maj coordonnées
        coordXPlayer = newCoordX;
        coordYPlayer = newCoordY;

        if (!clueEnabled ) {
            if(!MEMORY_NOCLUE){ // No clue, no memory
                //Mask old coord maze surroundings
                for (int i = -VISION; i <= VISION; i++) {
                    for (int j = -VISION; j <= VISION; j++) {
                        if ((prevCoordX + i >= 0) && (prevCoordX + i < tabLabyrinth.length) && (prevCoordY + j >= 0) && (prevCoordY + j < tabLabyrinth.length)) {
                            tabButton[prevCoordX + i][prevCoordY + j].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthMask"), null, null, null, null, null));
                        }
                    }
                }
            }
            //Display new coord maze surroundings
            displaySurroundings(newCoordX, newCoordY, VISION);
        }
        else {
            if(!MEMORY_CLUE){
                //Mask old coord maze surroundings
                for (int i = -VISION_HELP; i <= VISION_HELP; i++) {
                    for (int j = -VISION_HELP; j <= VISION_HELP; j++) {
                        if ((prevCoordX + i >= 0) && (prevCoordX + i < tabLabyrinth.length) && (prevCoordY + j >= 0) && (prevCoordY + j < tabLabyrinth.length)) {
                            tabButton[prevCoordX + i][prevCoordY + j].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthMask"), null, null, null, null, null));
                        }
                    }
                }
            }
            //Display new coord maze surroundings
            displaySurroundings(newCoordX, newCoordY, VISION_HELP);
        }
    }

    public void displaySurroundings(int coordX, int coordY, int vision) {
        if(!isHost()){
            for (int i = -vision; i <= vision; i++) {
                for (int j = -vision; j <= vision; j++) {
                    if ((coordX + i >= 0) && (coordX + i < tabLabyrinth.length) && (coordY + j >= 0) && (coordY + j < tabLabyrinth.length) ) {
                        // Formule rayon d'un cercle
                        double val = Math.pow(i,2)+Math.pow(j,2);
                        if (Math.sqrt(val) <= vision){
                            updateDisplayCell(coordX + i, coordY + j);
                        }
                    }
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
                        if(isHost()){
                            updateDisplayClue(coordX,coordY);
                        }
                        else {
                            if (shuffledPassword.equals("") ) {
                                tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthPath"), null, null, null, null, null));
                            } else {
                                tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthClue" + tabLabyrinth[coordX][coordY]), null, null, null, null, null));
                            }
                        }
                        break;
                }
            }
    }

    public void chargerIndice() {
        clueEnabled = true;
        displaySurroundings(coordXPlayer, coordYPlayer, VISION_HELP);
        if(isHost()){
            updateDisplayPlayerHost( receivedX, receivedY);
        }
    }

    public void updateDisplayClue(int coordX, int coordY){
        System.out.println("IN !" + passwordIndex);
        int count=0;
        for(int i = 0 ; i < tabLabyrinth.length ; i++){
            for(int j = 0 ; j < tabLabyrinth.length ; j++) {
                if(i == coordX && j == coordY){
                    tabButton[coordX][coordY].setStyle(new ImageButton.ImageButtonStyle(skinLabyrinthe.getDrawable("LabyrinthClue" + (1+Character.getNumericValue(passwordIndex.charAt(count)))), null, null, null, null, null));
                }
                switch (tabLabyrinth[i][j]){
                    case WALL: //if the read character corresponds to a wall
                    case PATH: // if the read character corresponds to an available path
                    case START: //if the read character corresponds to the starting place
                        break;
                    default:
                        count++;
                        break;
                }
            }
        }
        System.out.println("OUT : "+coordX+" - "+coordY + " - "+count);
    }


    public void updateDisplayPlayerHost(int newX, int newY) {
        int oldX=coordXPlayer;
        int oldY=coordYPlayer;
        coordXPlayer=newX;
        coordYPlayer=newY;
        updateDisplayCell(oldX,oldY);
        updateDisplayCell(coordXPlayer,coordYPlayer);
    }

    public void traiterMessage(String msg){
        String[] tokens = msg.split(":");
        // FLAG CODE : Host => Client
        if (tokens[0].equals("CODE")) {
            password=tokens[1];
            passwordIndex=tokens[2];
            shuffledPassword=tokens[3];
            // Setting the password
            this.setSolution(Integer.parseInt(password));
            int val = numero+1;
            System.out.println("Labyrinthe "+val+" password : "+password);
            try {
                lock_com_queue.put(new Object());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // FLAG MOVEMENT : Client => Host
        else if (tokens[0].equals("MOVEMENT")) {
                receivedX=Integer.parseInt(tokens[1]);
                receivedY=Integer.parseInt(tokens[2]);
            if(clueEnabled || DISPLAY_ON_HOST_PRECLUE){
                updateDisplayPlayerHost( receivedX, receivedY);
            }
        }
    }

    public void freeLock(){
        lockFreed = true;
        try {
            lock_com_queue.put(new Object());
            lock_algo_queue.put(new Object());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        textureAtlas.dispose();
        skinLabyrinthe.dispose();
    }

}
