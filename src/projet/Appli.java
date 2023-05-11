package projet;

import javax.sql.DataSource;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import jfox.context.ContextGlobal;
import jfox.context.IContext;
import jfox.javafx.util.UtilFX;
import jfox.jdbc.DataSourceSingleConnection;
import projet.commun.IMapper;
import projet.report.ManagerReport;
import projet.view.ManagerGui;


public class Appli extends Application {

	
	// Champs
	
	private IContext context;
	
	
	// Actions
	
	@Override
	public final void start(Stage stagePrimary) {

		try {

			// JDBC - DataSource
			DataSource dataSource = new DataSourceSingleConnection( "/META-INF/jdbc.properties" );
			
			// Context
			IContext context = new ContextGlobal();
			context.addBean( dataSource );
			context.addBean( IMapper.INSTANCE );

			// ManagerGui
	    	ManagerGui managerGui = context.getBean( ManagerGui.class );
	    	managerGui.setFactoryController( context::getBeanNew );
			managerGui.setStage( stagePrimary );
			managerGui.configureStage();
			
			// Configure le ManagerReport
			context.getBean( ManagerReport.class );
			
			// Affiche le stage
			stagePrimary.show();
			
		} catch(Exception e) {
			UtilFX.unwrapException(e).printStackTrace();
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setHeaderText( "Impossible de démarrer l'application." );
	        alert.showAndWait();
	        Platform.exit();
		}

	}
	
	@Override
	public final void stop() throws Exception {
		if (context != null ) {
			context.close();
		}
	}

	
	// Classe auxiliaire
	
	public static class MainProjet {
		public static void main(String[] args) {
			Application.launch( Appli.class, args);
		}
	}

}
