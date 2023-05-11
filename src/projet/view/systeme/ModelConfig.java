package projet.view.systeme;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import jfox.exception.ExceptionValidation;

public class ModelConfig {
	
	
	// Champs
	
	private File	dossierSchemas;
	
	
	// Initialisations
	
	@PostConstruct
	public void init() {

		// Lecture du fichier de configuration
		Properties props = new Properties();
		String chemin = "META-INF/config.properties";
		InputStream in = ClassLoader.getSystemResourceAsStream( chemin );
		if (in == null ) {
			throw new ExceptionValidation("Impossible de charger le fichier de configuration :\n"
					+ chemin );
		}
		try {
			props.load( in );
			in.close();
		} catch (IOException e) {
			throw new RuntimeException( e );
		}

		String valeur = props.getProperty( "dossier.schemas" );
		if ( valeur == null ) {
			throw new ExceptionValidation( "Paramètre absent dans le fichier de configuration :\n"
					+ "dossier.schemas" );
		}
		dossierSchemas = new File( valeur );
		if ( ! dossierSchemas.exists() ) {
			dossierSchemas.mkdirs();
		}
		if ( ! dossierSchemas.exists() || ! dossierSchemas.isDirectory() ) {
			throw new ExceptionValidation("Le dossier des schémas n'existe pas  :\n"
					+ dossierSchemas.toString() );
		}
	}
	
	
	// Getters
	
	public File getDossierSchemas() {
		return dossierSchemas;
	}

}
