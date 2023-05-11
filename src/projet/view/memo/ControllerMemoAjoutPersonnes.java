package projet.view.memo;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import jfox.javafx.view.Controller;
import jfox.javafx.view.IManagerGui;
import projet.data.Personne;


public class ControllerMemoAjoutPersonnes extends Controller {
	
	
	// Composants de la vue

	@FXML
	private ListView<Personne>	listView;
	@FXML
	private Button			buttonAjouter;


	// Autres champs
	
	@Inject
	private IManagerGui		managerGui;
	@Inject
	private ModelMemo		modelMemo;
	
	
	// Initialisation du Controller

	@FXML
	private void initialize() {

		// Data binding
		listView.setItems( modelMemo.getPersonnesPourDialogAjout() );
		listView.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		
		// Configuraiton des boutons
		listView.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldVal, newVal) -> {
					configurerBoutons();
		});
		configurerBoutons();
		
	}
	
	public void refresh() {
		modelMemo.actualiserListePersonnesPourDialogAjout();
		listView.requestFocus();
	}

	
	// Actions
	
	@FXML
	private void doAjouter() {
		modelMemo.ajouterPersonnes( listView.getSelectionModel().getSelectedItems() );
		managerGui.closeDialog();
	}

	@FXML
	private void doFermer() {
		managerGui.closeDialog();
	}
	
	
	// Gestion des évènements

	// Clic sur la liste
	@FXML
	private void gererClicSurListe( MouseEvent event ) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				if ( listView.getSelectionModel().getSelectedIndex() == -1 ) {
					managerGui.showDialogError( "Aucun élément n'est sélectionné dans la liste.");
				} else {
					doAjouter();
				}
			}
		}
	}

	
	// Méthodes auxiliaires
	
	private void configurerBoutons() {
		
    	if( listView.getSelectionModel().getSelectedItems().isEmpty() ) {
			buttonAjouter.setDisable(true);
		} else {
			buttonAjouter.setDisable(false);
		}
	}

}
