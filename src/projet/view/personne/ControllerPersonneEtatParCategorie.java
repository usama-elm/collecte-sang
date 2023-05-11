package projet.view.personne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import jfox.javafx.util.UtilFX;
import jfox.javafx.view.Controller;
import jfox.javafx.view.IManagerGui;
import projet.data.Categorie;
import projet.report.EnumReport;
import projet.report.ManagerReport;


public class ControllerPersonneEtatParCategorie extends Controller {
	
	
	// Composants de la vue

	@FXML
	private ListView<Categorie>	listView;
	@FXML
	private Button				buttonEtat1;


	// Autres champs
	
	@Inject
	private IManagerGui			managerGui;
	@Inject
	private ManagerReport		managerReport;
	@Inject
	private ModelCategorie		modelCategorie;
	
	
	// Initialisation du Controller

	@FXML
	private void initialize() {

		// Data binding
		listView.setItems( modelCategorie.getListe() );
		
		listView.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		
		// Configuraiton des boutons
		listView.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldVal, newVal) -> {
					configurerBoutons();
		});
		configurerBoutons();
		
	}
	
	public void refresh() {
		modelCategorie.actualiserListe();
		UtilFX.selectInListView( listView, modelCategorie.getSelection() );
		listView.requestFocus();
	}

	
	// Actions
	
	@FXML
	private void doEtat1() {
		Map<String, Object> params = new HashMap<>();
		params.put( "idCategorie", listView.getSelectionModel().getSelectedItem().getId() );
		managerReport.showViewer( EnumReport.PersonneParCategorie1, params );
	}	
	
	@FXML
	private void doEtat2() {
		Map<String, Object> params = new HashMap<>();
		List<Integer> liste = new ArrayList<>();
		for ( Categorie c : listView.getSelectionModel().getSelectedItems() ) {
			liste.add( c.getId() );
		}
		params.put( "idCategories", liste );
		managerReport.showViewer( EnumReport.PersonneParCategorie2, params );
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
					doEtat1();
				}
			}
		}
	}

	
	// Méthodes auxiliaires
	
	private void configurerBoutons() {
		
    	if( listView.getSelectionModel().getSelectedItems().isEmpty() ) {
			buttonEtat1.setDisable(true);
		} else {
			buttonEtat1.setDisable(false);
		}
	}

}
