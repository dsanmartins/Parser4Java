package br.parser.ui;

import javafx.scene.Node;
import javafx.scene.control.Tab;

public class Help extends Tab{

	public Help(String text, Node graphic) {


		this.setText(text);
		this.setGraphic(graphic);
		this.init();
	}

	public void init() {
		
		this.setClosable(false);
	
	}
	
}
