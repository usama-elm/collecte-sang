package projet.view;

import javax.annotation.PostConstruct;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.util.Duration;
import jfox.javafx.util.Converter;
import jfox.javafx.view.IEnumView;
import jfox.javafx.view.ManagerGuiAbstract;
import jfox.javafx.view.View;


public class ManagerGui extends ManagerGuiAbstract {
	
	
	// Initialisation
	
	@PostConstruct
	public void init() {
		messageAnomaly = "Echec du traitement demandé !";
		Converter.setDefaultMessage( "Valeur incorrecte !" );
	}

	
	// Actions

	@Override
	public void configureStage()  {
		
		// Choisit la vue à afficher
		showView( EnumView.Connexion);
		
		// Configure le stage
		stage.setTitle( "Gestion de contacts" );
		stage.setWidth(600);
		stage.setHeight(440);
		stage.setMinWidth(400);
		stage.setMinHeight(300);
		stage.getIcons().add(new Image(getClass().getResource("icone.png").toExternalForm()));
		
		// Configuration par défaut pour les boîtes de dialogue
		typeConfigDialogDefault = ConfigDialog.class;
	}

	
	@Override
	public Scene createScene( View view ) {
		BorderPane paneMenu = new BorderPane( view.getRoot() );
		paneMenu.setTop( (MenuBarAppli) factoryController.call( MenuBarAppli.class ) );
		Scene scene = new Scene( paneMenu );
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		return scene;
	}
	
	
	@Override
	public void showView(IEnumView idView) {

		double height = stage.getHeight();
		
		super.showView(idView);

		if ( Screen.getPrimary().getOutputScaleX() > 1. && ! Double.isNaN( height ) ) {
			
	    	WritableValue<Double> writableHeight = new WritableValue<Double>() {
	    	    @Override
	    	    public Double getValue() {
	    	        return stage.getHeight();
	    	    }
	    	    @Override
	    	    public void setValue(Double value) {
	    	        stage.setHeight(value);
	    	    }
	    	};	
	    	
	    	Timeline timeline = new Timeline();
		    timeline.getKeyFrames().addAll(
	    	        new KeyFrame( new Duration(20), 
        	            new KeyValue( writableHeight, height )
	    	        )
	    	    );
	    	stage.setHeight( height + 0.1 );
	    	timeline.play();    	
			
		}
		
	}
	
}