package projet.view.service;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import jfox.javafx.util.ConverterInteger;
import jfox.javafx.view.Controller;
import jfox.javafx.view.IManagerGui;
import projet.data.Service;
import projet.view.EnumView;


public class ControllerServiceForm extends Controller {

	
	// Composants de la vue
	
	@FXML
	private TextField		textFieldId;
	@FXML
	private TextField		textFieldNom;
	@FXML
	private TextField		textFieldAnneeCreation;
	@FXML
	private CheckBox		checkBoxSiege;

	
	// Autres champs
	
	@Inject
	private IManagerGui		managerGui;
	@Inject
	private ModelService	modelService;


	// Initialisation du Controller

	@FXML
	private void initialize() {
		
		Service courant = modelService.getCourant();

		// Data binding

//		textFieldId.textProperty().bindBidirectional( courant.idProperty(), new IntegerStringConverter()  );
//		textFieldNom.textProperty().bindBidirectional( courant.nomProperty() );
//		bindBidirectional( textFieldAnneeCreation, courant.anneeCreationProperty(), new ConverterInteger( "###0" ) );
//		checkBoxSiege.selectedProperty().bindBidirectional( courant.flagSiegeProperty() );

		bindBidirectional( textFieldId, courant.idProperty(), new ConverterInteger() );
		bindBidirectional( textFieldNom, courant.nomProperty() );
		bindBidirectional( textFieldAnneeCreation, courant.anneeCreationProperty(), new ConverterInteger( "###0" ) );
		bindBidirectional( checkBoxSiege, courant.flagSiegeProperty() );
		
	}
	
	
	public void refresh() {
		modelService.actualiserCourant();
	}
	
	
	// Actions
	
	@FXML
	private void doAnnuler() {
		managerGui.showView( EnumView.ServiceListe );
	}
	
	@FXML
	private void doValider() {
		modelService.validerMiseAJour();
		managerGui.showView( EnumView.ServiceListe );
	}
}
