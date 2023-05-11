package projet.view.test;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import jfox.javafx.util.UtilFX;
import jfox.javafx.view.Controller;
import projet.dao.DaoService;


public class ControllerTestDaoService extends Controller {
	
	
	// Composants visuales
	
	@FXML
	private TextArea		textArea;
	
	
	// Autres champs
	
	@Inject
	private DaoService			dao;
	
	private final int		id = 1;	
	
	
	// Actions
	
	@FXML
	private void doLister() {
		textArea.clear();
		for (Object item : dao.listerTout() ) {
			textArea.appendText( UtilFX.objectToString( item ) );
			textArea.appendText( "\n"  );
		}
		textArea.appendText( "\n"  );
		textArea.appendText(  "Test n°1 OK \n");;
	}
	
	@FXML
	private void doRetrouver() {
		textArea.clear();
		textArea.appendText( UtilFX.objectToString( dao.retrouver( id ) ) );
		textArea.appendText( "\n\n"  );
		textArea.appendText(  "Test n°2 OK \n");;
	}
	
	@FXML
	private void doModifier() {
		textArea.clear();
		dao.modifier( dao.retrouver( id ) );
		textArea.appendText(  "Test n°3 OK \n");;
	}
	
	@FXML
	private void doInsererSupprimer() {
		textArea.clear();
		int idNouveau = dao.inserer( dao.retrouver( id ));
		textArea.appendText( UtilFX.objectToString( dao.retrouver( idNouveau ) ) );
		dao.supprimer( idNouveau );
		textArea.appendText( "\n\n"  );
		textArea.appendText(  "Test n°4 OK \n");;
	}
	
}
