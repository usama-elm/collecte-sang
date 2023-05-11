package projet.view.personne;

import javax.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfox.exception.ExceptionValidation;
import jfox.javafx.util.UtilFX;
import projet.commun.IMapper;
import projet.dao.DaoCategorie;
import projet.dao.DaoPersonne;
import projet.data.Categorie;


public class ModelCategorie  {
	
	
	// Données observables 
	
	private final ObservableList<Categorie> liste = FXCollections.observableArrayList(); 
	
	private final Categorie	courant = new Categorie();

	
	// Autres champs
	
	private Categorie		selection;

	@Inject
	private IMapper			mapper;
    @Inject
	private DaoCategorie	daoCategorie;
    @Inject
    private DaoPersonne		daoPersonne;
	
	
	// Getters & Setters
	
	public ObservableList<Categorie> getListe() {
		return liste;
	}
	
	public Categorie getCourant() {
		return courant;
	}
	
	public Categorie getSelection() {
		return selection;
	}
	
	public void setSelection( Categorie selection ) {
		if ( selection == null ) {
			this.selection = new Categorie();
		} else {
			this.selection = daoCategorie.retrouver( selection.getId() );
		}
	}
	
	
	// Actions
	
	public void actualiserListe() {
		liste.setAll( daoCategorie.listerTout() );
 	}

	
	public void actualiserCourant() {
		mapper.update( courant, selection );
	}
	
	
	public void validerMiseAJour() {

		// Vérifie la validité des données
		
		StringBuilder message = new StringBuilder();

		if( courant.getLibelle() == null || courant.getLibelle().isEmpty() ) {
			message.append( "\nLe libellé ne doit pas être vide." );
		} else  if ( courant.getLibelle().length()> 25 ) {
			message.append( "\nLe libellé est trop long : 25 maxi." );
		}
		
		if ( message.length() > 0 ) {
			throw new ExceptionValidation( message.toString().substring(1) );
		}
		
		
		// Effectue la mise à jour
		
		if ( courant.getId() == null ) {
			// Insertion
			selection.setId( daoCategorie.inserer( courant ) );
		} else {
			// modficiation
			daoCategorie.modifier( courant );
		}
	}
	
	
	public void supprimer( Categorie item ) {
		
		// Vérifie l'abence de personnes rattachées à la catégorie
		if ( daoPersonne.compterPourCategorie( item.getId() ) != 0 ) {
			throw new ExceptionValidation( "Des personnes sont rattachées à cette catégorie.." ) ;
		}
		
		daoCategorie.supprimer( item.getId() );
		selection = UtilFX.findNext( liste, item );
	}
	
}
