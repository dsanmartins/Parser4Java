package br.parser.ui;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class About extends Tab{

	// Create a TextArea an initial text
		
	
	public About(String text, Node graphic) {


		this.setText(text);
		this.setGraphic(graphic);
		this.init();
	}
	
	public void init() {
		
		TextArea right = new TextArea("Copyright [2018] [Daniel Gustavo San Martín Santibáñez]\n" + 
				"\n" + 
				"Licensed under the Apache License, Version 2.0 (the \"License\");\n" + 
				"you may not use this file except in compliance with the License.\n" + 
				"You may obtain a copy of the License at\n" + 
				"\n" + 
				"    http://www.apache.org/licenses/LICENSE-2.0\n" + 
				"\n" + 
				"Unless required by applicable law or agreed to in writing, software\n" + 
				"distributed under the License is distributed on an \"AS IS\" BASIS,\n" + 
				"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either\n"
				+ " express or implied. See the License for the specific language\n" + 
				" governing permissions and limitations under the License.\n");
		
		right.setPrefRowCount(14);
		right.setMaxWidth(460);
		right.setEditable(false);
		this.setClosable(false);
		
		
		VBox vbox = new VBox(right);
		this.setContent(vbox);
	}
}
