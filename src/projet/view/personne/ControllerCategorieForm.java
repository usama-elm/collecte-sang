package projet.view.personne;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import jfox.javafx.util.ConverterInteger;
import jfox.javafx.view.Controller;
import jfox.javafx.view.IManagerGui;
import projet.data.Categorie;
import projet.view.EnumView;


public class ControllerCategorieForm extends Controller {

	
	// Composants de la vue
	
	@FXML
	private TextField			textFieldId;
	@FXML
	private TextField			textFieldLibelle;


	// Autres champs
	
	@Inject
	private IManagerGui			managerGui;
	@Inject
	private ModelCategorie		modelCategorie;


	// Initialisation du Controller

	@FXML
	private void initialize() {
		
		Categorie courant = modelCategorie.getCourant();

		// Data binding
		textFieldId.textProperty().bindBidirectional( courant.idProperty(), new ConverterInteger() );
		textFieldLibelle.textProperty().bindBidirectional( courant.libelleProperty()  );
	}
	
	
	public void refresh() {
		modelCategorie.actualiserCourant();
	}
	
	
	// Actions
	
	@FXML
	private void doAnnuler() {
		managerGui.showView( EnumView.CategorieListe );
	}
	
	@FXML
	private void doValider() {
		modelCategorie.validerMiseAJour();
		managerGui.showView( EnumView.CategorieListe );
	}

}
