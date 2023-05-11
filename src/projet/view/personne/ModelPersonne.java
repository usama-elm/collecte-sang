package projet.view.personne;

import javax.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfox.exception.ExceptionValidation;
import jfox.javafx.util.UtilFX;
import projet.commun.IMapper;
import projet.dao.DaoPersonne;
import projet.data.Personne;
import projet.data.Telephone;


public class ModelPersonne {
	
	
	// Données observables 
	
	private final ObservableList<Personne> liste = FXCollections.observableArrayList();
	
	private final Personne	courant = new Personne();
	
	
	// Autres champs
	
	private Personne		selection;

	@Inject
	private IMapper			mapper;
    @Inject
	private DaoPersonne		daoPersonne;
	
	
	// Getters & Setters
	
	public ObservableList<Personne> getListe() {
		return liste;
	}
	
	public Personne getCourant() {
		return courant;
	}
	
	public Personne getSelection() {
		return selection;
	}
	
	public void setSelection( Personne selection ) {
		if ( selection == null ) {
			this.selection = new Personne();
		} else {
			this.selection = daoPersonne.retrouver( selection.getId() );
		}
	}

	
	// Actions
	
	public void actualiserListe() {
		liste.setAll( daoPersonne.listerTout() );
	}

	
	public void actualiserCourant() {
		mapper.update( courant, selection );
	}

	
	public void validerMiseAJour() {

		// Vérifie la validité des données
		
		StringBuilder message = new StringBuilder();
		
		if( courant.getNom() == null || courant.getNom().isEmpty() ) {
			message.append( "\nLe nom ne doit pas être vide." );
		} else  if ( courant.getNom().length()> 25 ) {
			message.append( "\nLe nom est trop long." );
		}

		if( courant.getPrenom() == null || courant.getPrenom().isEmpty() ) {
			message.append( "\nLe prénom ne doit pas être vide." );
		} else if ( courant.getPrenom().length()> 25 ) {
			message.append( "\nLe prénom est trop long." );
		}

		if( courant.getCategorie() == null ) {
			message.append( "\nLe catégorie doit être indiquée." );
		}
		
		if ( message.length() > 0 ) {
			throw new ExceptionValidation( message.toString().substring(1) );
		}

		
		// Effectue la mise à jour
		
		if ( courant.getId() == null ) {
			// Insertion
			selection.setId( daoPersonne.inserer( courant ) );
		} else {
			// modficiation
			daoPersonne.modifier( courant );
		}
	}
	

	public void supprimer( Personne item ) {
		daoPersonne.supprimer( item.getId() );
		selection = UtilFX.findNext( liste, item );
	}
	

	public void ajouterTelephone() {
		courant.getTelephones().add( new Telephone() );
	}
	

	public void supprimerTelephone( Telephone telephone )  {
		courant.getTelephones().remove( telephone );
	}
	
}
