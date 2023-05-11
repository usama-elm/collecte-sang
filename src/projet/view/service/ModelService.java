package projet.view.service;

import javax.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfox.exception.ExceptionValidation;
import jfox.javafx.util.UtilFX;
import projet.commun.IMapper;
import projet.dao.DaoService;
import projet.data.Service;


public class ModelService  {
	
	
	// Données observables 
	
	private final ObservableList<Service> liste = FXCollections.observableArrayList(); 
	
	private final Service	courant = new Service();

	
	// Autres champs
	
	private Service		selection;

	@Inject
	private IMapper		mapper;
    @Inject
	private DaoService	daoService;
	
	
	// Getters & Setters
	
	public ObservableList<Service> getListe() {
		return liste;
	}
	
	public Service getCourant() {
		return courant;
	}
	
	public Service getSelection() {
		return selection;
	}
	
	public void setSelection( Service selection ) {
		if ( selection == null ) {
			this.selection = new Service();
		} else {
			this.selection = daoService.retrouver( selection.getId() );
		}
	}
	
	
	// Actions
	
	public void actualiserListe() {
		liste.setAll( daoService.listerTout() );
 	}

	
	public void actualiserCourant() {
		mapper.update( courant, selection );
	}
	
	
	public void validerMiseAJour() {

		// Vérifie la validité des données
		
		StringBuilder message = new StringBuilder();

		if( courant.getNom() == null || courant.getNom().isEmpty() ) {
			message.append( "\nLe nom ne doit pas être vide." );
		} else  if ( courant.getNom().length()> 50 ) {
			message.append( "\nLe nom est trop long : 50 maxi." );
		}

		if( courant.getAnneeCreation() != null ) {
			if ( courant.getAnneeCreation() < 1900  
					|| courant.getAnneeCreation() > 2100 ) {
				message.append( "\nValeur incorrecte pour l'année de création." );
			}
		}
		
		if ( message.length() > 0 ) {
			throw new ExceptionValidation( message.toString().substring(1) );
		}
		
		
		// Effectue la mise à jour
		
		if ( courant.getId() == null ) {
			// Insertion
			selection.setId( daoService.inserer( courant ) );
		} else {
			// modficiation
			daoService.modifier( courant );
		}
	}
	
	
	public void supprimer( Service item ) {
		daoService.supprimer( item.getId() );
		selection = UtilFX.findNext( liste, item );
	}
	
}
