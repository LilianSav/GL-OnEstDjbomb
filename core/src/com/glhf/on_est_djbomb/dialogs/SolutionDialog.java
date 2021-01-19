package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class SolutionDialog extends Dialog{
	private final Skin skin;
    Table solutionContentTable = getContentTable();

    public SolutionDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    public void initContent() {
        // Section Content
    	solutionContentTable.row();

        // Section button
        button("Retour");
    }

    public void setSolution(String string) {
    	solutionContentTable.clearChildren();
    	solutionContentTable.add(new Label(string, skin));
    }

    @Override
    protected void result(Object object) {

    }
}
