package projet.view.memo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import jfox.exception.ExceptionValidation;
import jfox.javafx.util.UtilFX;
import projet.commun.IMapper;
import projet.dao.DaoMemo;
import projet.dao.DaoPersonne;
import projet.data.Memo;
import projet.data.Personne;
import projet.view.systeme.ModelConfig;


public class ModelMemo  {
	
	
	// Données observables 
	
	private final ObservableList<Memo> liste = FXCollections.observableArrayList(); 
	
	private final Memo	courant = new Memo();
	
	private final ObservableList<Personne> personnesPourDialogAjout = FXCollections.observableArrayList();
	
	private final Property<Image>	schema = new SimpleObjectProperty<>();

	
	// Autres champs
	
	private Memo		selection;

	@Inject
	private IMapper		mapper;
    @Inject
	private DaoMemo		daoMemo;
    @Inject
    private DaoPersonne	daoPersonne;
    @Inject
    private ModelConfig	modelConfig;
    
	private boolean flagModifSchema;
    
	
	// Initialisations
	
	@PostConstruct
	public void init() {
		schema.addListener( obs -> flagModifSchema = true );
	}
	
	
	// Getters & Setters
	
	public ObservableList<Memo> getListe() {
		return liste;
	}
	
	public Memo getCourant() {
		return courant;
	}
	
	public ObservableList<Personne> getPersonnesPourDialogAjout() {
		return personnesPourDialogAjout;
	}
	
	public Memo getSelection() {
		return selection;
	}
	
	public void setSelection( Memo selection ) {
		if ( selection == null ) {
			this.selection = new Memo();
		} else {
			this.selection = daoMemo.retrouver( selection.getId() );
		}
	}
	
	public Property<Image> schemaPropert() {
		return schema;
	}
	
	
	// Actions
	
	public void actualiserListe() {
		liste.setAll( daoMemo.listerTout() );
 	}
	
	public void actualiserListePersonnesPourDialogAjout() {
		personnesPourDialogAjout.setAll( daoPersonne.listerTout() );
		personnesPourDialogAjout.removeAll( courant.getPersonnes() );
 	}

	
	public void actualiserCourant() {
		mapper.update( courant, selection );
		File chemin = getCheminSchemaCourant();
		if ( chemin.exists() ) {
		    schema.setValue( new Image( chemin.toURI().toString() ) );
		} else {
		    schema.setValue( null );
		}
		flagModifSchema = false;
	}
	
	
	public void validerMiseAJour() {

		// Vérifie la validité des données
		
		StringBuilder message = new StringBuilder();

		if( courant.getTitre() == null || courant.getTitre().isEmpty() ) {
			message.append( "\nLe titre ne doit pas être vide." );
		} else  if ( courant.getTitre().length()> 50 ) {
			message.append( "\nLe titre est trop long : 50 maxi." );
		}

		if( courant.getEffectif() != null  ) {
			if( courant.getEffectif() < 0  ) {
				message.append( "\nL'effectif ne peut pas être inférieur à zéro." );
			} else  if ( courant.getEffectif() > 1000 ) {
				message.append( "\nEffectif trop grand : 1000 maxi." );
			}
		}

		if( courant.getBudget() != null  ) {
			if( courant.getBudget() < 0  ) {
				message.append( "\nLe budget ne peut pas être inférieur à zéro." );
			} else  if ( courant.getBudget() > 1000000 ) {
				message.append( "\nBudget trop grand : 1 000 000 maxi." );
			}
		}

		if( courant.getEcheance() != null  ) {
			if( courant.getEcheance().isBefore( LocalDate.of(2000, 1, 1) ) 
					|| courant.getEcheance().isAfter( LocalDate.of(2099, 12, 31) ) ) {
				message.append( "\nLa date d'échéance doit être comprise entre le 01/01/2000 et le 31/12/2099." );
			}
		}
		
		if ( message.length() > 0 ) {
			throw new ExceptionValidation( message.toString().substring(1) );
		}
		
		
		// Effectue la mise à jour
		
		if ( courant.getId() == null ) {
			// Insertion
			selection.setId( daoMemo.inserer( courant ) );
		} else {
			// modficiation
			daoMemo.modifier( courant );
		}
		
		if ( flagModifSchema ) {
			if (schema.getValue() == null) {
				getCheminSchemaCourant().delete();
			} else {
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(schema.getValue(), null), "JPG", getCheminSchemaCourant());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} 
		}
	}
	
	
	public void supprimer( Memo item ) {
		daoMemo.supprimer( item.getId() );
		selection = UtilFX.findNext( liste, item );
		getCheminSchemaCourant().delete();
	}
	
	
	public void supprimerPersonnes( List<Personne> items ) {
		courant.getPersonnes().removeAll( items );
	}
	
	
	public void ajouterPersonnes( List<Personne> items ) {
		courant.getPersonnes().addAll( items );
	}
	
	
	// Métodes auxiliaires
	
	public File getCheminSchemaCourant() {
		String nomFichier = String.format( "%06d.jpg", courant.getId() );
		File dossierSchemas = modelConfig.getDossierSchemas();
		return new File( dossierSchemas, nomFichier );
	}
}
