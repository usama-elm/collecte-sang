package projet.view.memo;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import jfox.javafx.util.UtilFX;
import jfox.javafx.view.Controller;
import jfox.javafx.view.IManagerGui;
import projet.data.Memo;
import projet.view.EnumView;


public class ControllerMemoListe extends Controller {
	
	
	// Composants de la vue

	@FXML
	private ListView<Memo>	listView;
	@FXML
	private Button			buttonModifier;
	@FXML
	private Button			buttonSupprimer;


	// Autres champs
	
	@Inject
	private IManagerGui		managerGui;
	@Inject
	private ModelMemo		modelMemo;
	
	
	// Initialisation du Controller

	@FXML
	private void initialize() {

		// Data binding
		listView.setItems( modelMemo.getListe() );
		
		// Cell factory
		UtilFX.setCellFactory( listView, item -> item.getTitre() );
		
		// Configuraiton des boutons
		listView.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldVal, newVal) -> {
					configurerBoutons();
		});
		configurerBoutons();
		
	}
	
	public void refresh() {
		modelMemo.actualiserListe();
		UtilFX.selectInListView( listView, modelMemo.getSelection() );
		listView.requestFocus();
	}

	
	// Actions
	
	@FXML
	private void doAjouter() {
		modelMemo.setSelection( null );
		managerGui.showView( EnumView.MemoForm );
	}

	@FXML
	private void doModifier() {
		modelMemo.setSelection( listView.getSelectionModel().getSelectedItem() );
		managerGui.showView( EnumView.MemoForm );
	}

	@FXML
	private void doSupprimer() {
		if ( managerGui.showDialogConfirm( "Confirmez-vous la suppresion ?" ) ) {
			modelMemo.supprimer( listView.getSelectionModel().getSelectedItem() );
			refresh();
		}
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
					doModifier();
				}
			}
		}
	}

	
	// Méthodes auxiliaires
	
	private void configurerBoutons() {
		
    	if( listView.getSelectionModel().getSelectedItems().isEmpty() ) {
			buttonModifier.setDisable(true);
			buttonSupprimer.setDisable(true);
		} else {
			buttonModifier.setDisable(false);
			buttonSupprimer.setDisable(false);
		}
	}

}
