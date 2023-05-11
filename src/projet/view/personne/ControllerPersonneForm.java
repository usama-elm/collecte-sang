package projet.view.personne;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import jfox.javafx.control.EditingCell;
import jfox.javafx.util.ConverterInteger;
import jfox.javafx.view.Controller;
import jfox.javafx.view.IManagerGui;
import projet.data.Categorie;
import projet.data.Personne;
import projet.data.Telephone;
import projet.view.EnumView;


public class ControllerPersonneForm extends Controller  {
	
	
	// Composants de la vue
	
	@FXML
	private TextField			textFieldId;
	@FXML
	private TextField			textFieldNom;
	@FXML	
	private TextField			textFieldPrenom;
    @FXML
    private ComboBox<Categorie>	comboBoxCategorie;
	@FXML
	private TableView<Telephone>	tableViewTelphones;
	@FXML
	private TableColumn<Telephone, Integer> columnId;
	@FXML
	private TableColumn<Telephone, String> columnLibelle;
	@FXML
	private TableColumn<Telephone, String> columnNumero;

	
	// Autres champs
	@Inject
	private IManagerGui			managerGui;
	@Inject
	private ModelPersonne		modelPersonne;
	@Inject
	private ModelCategorie		modelCategorie;
    
	
	// Initialisation du controller
	
	public void initialize() {

		Personne courant = modelPersonne.getCourant();
		
		// Champs simples
		textFieldId.textProperty().bindBidirectional( courant.idProperty(), new ConverterInteger() );
		textFieldNom.textProperty().bindBidirectional( courant.nomProperty() );
		textFieldPrenom.textProperty().bindBidirectional( courant.prenomProperty() );
        
		// Combo box
		comboBoxCategorie.setItems( modelCategorie.getListe() );
        comboBoxCategorie.valueProperty().bindBidirectional( courant.categorieProperty() );
		
		// TableView
		tableViewTelphones.setItems(  modelPersonne.getCourant().getTelephones() );
		
		columnId.setCellValueFactory( t -> t.getValue().idProperty() );
		columnLibelle.setCellValueFactory( t -> t.getValue().libelleProperty() );
		columnNumero.setCellValueFactory( t -> t.getValue().numeroProperty() );

		columnLibelle.setCellFactory(  p -> new EditingCell<>() );
		columnNumero.setCellFactory(  p -> new EditingCell<>() );		
	
	}
	
	
	public void refresh() {
		modelCategorie.actualiserListe();
		modelPersonne.actualiserCourant();
	}
	
	
	// Actions
	
	@FXML
	private void doValider() {
		modelPersonne.validerMiseAJour();
		managerGui.showView( EnumView.PersonneListe );
	}
	
	@FXML
	private void doAnnuler() {
		managerGui.showView( EnumView.PersonneListe );
	}
	
	@FXML
	private void doAjouterTelephone() {
		modelPersonne.ajouterTelephone();
	}
	
	
	@FXML
	private void doiSupprimerTelephone() {
		Telephone telephone = tableViewTelphones.getSelectionModel().getSelectedItem();
		modelPersonne.supprimerTelephone(telephone);
	}
    
}
