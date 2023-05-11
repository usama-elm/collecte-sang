package projet.view.memo;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import jfox.javafx.util.ConverterDouble;
import jfox.javafx.util.ConverterInteger;
import jfox.javafx.util.ConverterLocalDate;
import jfox.javafx.view.Controller;
import jfox.javafx.view.IManagerGui;
import projet.data.Categorie;
import projet.data.Memo;
import projet.data.Personne;
import projet.view.EnumView;
import projet.view.personne.ModelCategorie;


public class ControllerMemoForm extends Controller {

	
	// Composants de la vue
	
	@FXML
	private TextField		textFieldId;
	@FXML
	private TextField		textFieldTitre;
	@FXML
	private TextArea		textAreaDescription;
	@FXML
	private CheckBox		checkBoxUrgent;
	@FXML
	private ToggleGroup		toggleGroupStatut;
	@FXML
	private TextField		textFieldEffectif;
	@FXML
	private TextField		textFieldBudget;
	@FXML
	private DatePicker		datePickerEcheance;
	@FXML
	private ComboBox<Categorie>	comboBoxCategorie;
	@FXML
	private ListView<Personne> listViewPersonnes;
	@FXML
	private ImageView		imageViewSchema;
	@FXML
	private Button			buttonOuvrirSchema;


	// Autres champs
	
	@Inject
	private IManagerGui		managerGui;
	@Inject
	private ModelMemo		modelMemo;
	@Inject
	private ModelCategorie	modelCategorie;


	// Initialisation du Controller

	@FXML
	private void initialize() {
		
		Memo courant = modelMemo.getCourant();

		// Data binding

		bindBidirectional( textFieldId, courant.idProperty(), new ConverterInteger() );
		bindBidirectional( textFieldTitre, courant.titreProperty() );
		bindBidirectional( textAreaDescription, courant.descriptionProperty() );
		bindBidirectional( checkBoxUrgent, courant.flagUrgentProperty() );

		comboBoxCategorie.setItems( modelCategorie.getListe() );
		bindBidirectional( comboBoxCategorie, courant.categorieProperty() );
		
		
		// Statut
		toggleGroupStatut.selectedToggleProperty().addListener( 
				obs -> actualiserStatutDansModele() );
		courant.statutProperty().addListener( obs -> actualiserStatutDansVue() );
		actualiserStatutDansVue();
		
		// Effectif
		bindBidirectional( textFieldEffectif, courant.effectifProperty(), new ConverterInteger() );

		// Budget
		bindBidirectional( textFieldBudget, courant.budgetProperty(), new ConverterDouble("#,##0.00", "Valeur incorrecte pour le budget !" ) );

		// Date d'échéance
		bindBidirectional( datePickerEcheance, courant.echeanceProperty(), new ConverterLocalDate() );
		
		// Liste des personnes
		listViewPersonnes.setItems( courant.getPersonnes() );
		listViewPersonnes.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		
		// Schéma
		bindBidirectional(imageViewSchema, modelMemo.schemaPropert() );
		
	}
	
	
	public void refresh() {
		modelMemo.actualiserCourant();
		modelCategorie.actualiserListe();
		if ( modelMemo.getCheminSchemaCourant().exists() ) {
			buttonOuvrirSchema.setDisable( false );
		} else {
			buttonOuvrirSchema.setDisable( true );
		}		
	}
	
	
	// Actions
	
	@FXML
	private void doSupprimerCategorie() {
		comboBoxCategorie.setValue( null );
	}
	
	@FXML
	private void doSupprimerPersonnes() {
		modelMemo.supprimerPersonnes( listViewPersonnes.getSelectionModel().getSelectedItems() );
	}
	
	@FXML
	private void doAjouterPersonnes() {
		managerGui.showDialog( EnumView.MemoAjoutPersonnes );
	}
	
	@FXML
	private void doChoisirSchema() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choisissez un fichier image");
		File chemin = fileChooser.showOpenDialog( managerGui.getStage() );
		if ( chemin != null ) {
			imageViewSchema.setImage( new Image( chemin.toURI().toString() ) );
		}
	}
		
	@FXML
	private void doOuvrirSchema() {
		try {
			Desktop.getDesktop().open( modelMemo.getCheminSchemaCourant() );
		} catch (IOException e) {
			throw new RuntimeException( e );
		}
	}
		
	@FXML
	private void doSupprimerSchema() {
		imageViewSchema.setImage( null );
	}	
	
	@FXML
	private void doAnnuler() {
		managerGui.showView( EnumView.MemoListe );
	}
	
	@FXML
	private void doValider() {
		modelMemo.validerMiseAJour();
		managerGui.showView( EnumView.MemoListe );
	}
	
	
	// Méthodes auxiliaires
	
	private void actualiserStatutDansModele() {
		// Modifie le statut en fonction du bouton radio sélectionné
		Toggle bouton = toggleGroupStatut.getSelectedToggle();
		int statut = toggleGroupStatut.getToggles().indexOf( bouton );
		modelMemo.getCourant().setStatut( statut );
	}
	
	private void actualiserStatutDansVue() {
		// Sélectionne le bouton radio correspondant au statut
		int statut = modelMemo.getCourant().getStatut();
		Toggle bouton = toggleGroupStatut.getToggles().get( statut );
		toggleGroupStatut.selectToggle(  bouton );
	}	

}
