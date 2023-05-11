package projet.view.compte;


import javax.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfox.exception.ExceptionValidation;
import jfox.javafx.util.UtilFX;
import projet.commun.IMapper;
import projet.dao.DaoCompte;
import projet.data.Compte;


public class ModelCompte {
	
	
	// Données observables 
	
	private final ObservableList<Compte> liste = FXCollections.observableArrayList(); 
	
	private final Compte	courant = new Compte();
	
	
	// Autres champs
	
	private Compte		selection;

    @Inject
	private IMapper		mapper;
    @Inject
	private DaoCompte	daoCompte;
	
	
	// Getters & Setters
	
	public ObservableList<Compte> getListe() {
		return liste;
	}

	public Compte getCourant() {
		return courant;
	}
	
	public Compte getSelection() {
		return selection;
	}
	
	public void setSelection( Compte selection ) {
		if ( selection == null ) {
			this.selection = new Compte();
		} else {
			this.selection = daoCompte.retrouver( selection.getId() );
		}
	}
	
	
	// Actions
	
	public void actualiserListe() {
		liste.setAll( daoCompte.listerTout() );
 	}

	
	public void actualiserCourant() {
		mapper.update( courant, selection );
	}
	
	
	public void validerMiseAJour() {

		// Vérifie la validité des données
		
		StringBuilder message = new StringBuilder();
		
		if( courant.getPseudo() == null || courant.getPseudo().isEmpty() ) {
			message.append( "\nLe pseudo ne doit pas être vide." );
		} else 	if ( courant.getPseudo().length() < 3 ) {
			message.append( "\nLe pseudo est trop court : 3 mini." );
		} else  if ( courant.getPseudo().length()> 25 ) {
			message.append( "\nLe pseudo est trop long : 25 maxi." );
		} else 	if ( ! daoCompte.verifierUnicitePseudo( courant.getPseudo(), courant.getId() ) ) {
			message.append( "\nLe pseudo " + courant.getPseudo() + " est déjà utilisé." );
		}
		
		if( courant.getMotDePasse() == null || courant.getMotDePasse().isEmpty() ) {
			message.append( "\nLe mot de passe ne doit pas être vide." );
		} else  if ( courant.getMotDePasse().length()< 3 ) {
			message.append( "\nLe mot de passe est trop court : 3 mini." );
		} else  if ( courant.getMotDePasse().length()> 25 ) {
			message.append( "\nLe mot de passe est trop long : 25 maxi." );
		}
		
		if( courant.getEmail() == null || courant.getEmail().isEmpty() ) {
			message.append( "\nL'adresse e-mail ne doit pas être vide." );
		} else  if ( courant.getEmail().length()> 100 ) {
			message.append( "\nL'adresse e-mail est trop longue : 100 maxi." );
		}
		
		if ( message.length() > 0 ) {
			throw new ExceptionValidation( message.toString().substring(1) );
		}
		
		
		// Effectue la mise à jour
		
		if ( courant.getId() == null ) {
			// Insertion
			selection.setId( daoCompte.inserer( courant ) );
		} else {
			// modficiation
			daoCompte.modifier( courant );
		}
	}
	
	
	public void supprimer( Compte item ) {
		daoCompte.supprimer( item.getId() );
		selection = UtilFX.findNext( liste, item );
	}

}
