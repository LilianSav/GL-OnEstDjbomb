package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ClueDialog extends Dialog{
	private final Skin skin;
	Table clueContentTable = getContentTable();
	
    public ClueDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    public void initContent(){
        // Section Content
        clueContentTable.row();

        // Section button
        button("Retour");
    }
    
    public void setClue(String clue) {
    	clueContentTable.clearChildren();
    	clueContentTable.add(new Label(clue, skin));
    }

    @Override
    protected void result(Object object) {

    }
}
