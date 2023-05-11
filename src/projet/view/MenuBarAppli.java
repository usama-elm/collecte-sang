package projet.view;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import jfox.javafx.view.IManagerGui;
import projet.commun.Roles;
import projet.data.Compte;
import projet.report.EnumReport;
import projet.report.ManagerReport;
import projet.view.systeme.ModelConnexion;


public class MenuBarAppli extends MenuBar {

	
	// Champs 
	
	private Menu	menuDonnees;
	private Menu	menuTests;
	private Menu	menuEtats;
	
	private MenuItem itemDeconnecter;

	private MenuItem itemCategories;
	private MenuItem itemComptes;
	
	@Inject
	private IManagerGui 	managerGui;
	@Inject
	private ManagerReport	managerReport;
	@Inject
	private ModelConnexion	modelConnexion;	
	
	
	// Initialisation
	
	@PostConstruct
	public void init() {

		
		// Variables de travail
		Menu menu;
		MenuItem item;
		
		
		// Manu Système
		
		menu =  new Menu( "Système" );;
		this.getMenus().add(menu);
		
		item = new MenuItem( "Se déconnecter" );
		item.setOnAction(  e -> managerGui.showView( EnumView.Connexion )  );
		menu.getItems().add( item );
		itemDeconnecter = item;
		
		item = new MenuItem( "Quitter" );
		item.setOnAction(  e -> managerGui.exit()  );
		menu.getItems().add( item );

		
		// Manu Données
		
		menu =  new Menu( "Données" );;
		this.getMenus().add(menu);
		menuDonnees = menu;
		
		item = new MenuItem( "Services" );
		item.setOnAction(  e -> managerGui.showView( EnumView.ServiceListe )  );
		menu.getItems().add( item );
		
		item = new MenuItem( "Mémos" );
		item.setOnAction(  e -> managerGui.showView( EnumView.MemoListe )  );
		menu.getItems().add( item );
		
		item = new MenuItem( "Personnes" );
		item.setOnAction(  e -> managerGui.showView( EnumView.PersonneListe )  );
		menu.getItems().add( item );
		
		item = new MenuItem( "Catégories" );
		item.setOnAction(  e -> managerGui.showView( EnumView.CategorieListe )  );
		menu.getItems().add( item );
		itemCategories = item;
		
		item = new MenuItem( "Comptes" );
		item.setOnAction(  e -> managerGui.showView( EnumView.CompteListe )  );
		menu.getItems().add( item );
		itemComptes = item;

		
		// Manu Etats
		
		menu =  new Menu( "Etats" );;
		this.getMenus().add(menu);
		menuEtats = menu;
		
		item = new MenuItem( "Liste des personnes (PDF)" );
		item.setOnAction(  e -> managerReport.openFilePdf( EnumReport.PersonneListeSimple, null) );
		menu.getItems().add( item );
		
		item = new MenuItem( "Liste des personnes (Viewer)" );
		item.setOnAction(  e -> managerReport.showViewer( EnumReport.PersonneListeSimple, null) );
		menu.getItems().add( item );

		item = new MenuItem( "Personnes par catégorie" );
		item.setOnAction(  e -> managerGui.showView( EnumView.PersonneEtatParCategorie ) );
		menu.getItems().add( item );
		
		item = new MenuItem( "Annuaire" );
		item.setOnAction(  e -> managerReport.openFilePdf( EnumReport.Annuaire, null) );
		menu.getItems().add( item );
		
		
		// Manu Tests
		
		menu =  new Menu( "Tests" );;
		this.getMenus().add(menu);
		menuTests = menu;
		
		item = new MenuItem( "DaoCategorie" );
		item.setOnAction(  e -> managerGui.showView( EnumView.TestDaoCategorie )  );
		menu.getItems().add( item );
		
		item = new MenuItem( "DaoMemo" );
		item.setOnAction(  e -> managerGui.showView( EnumView.TestDaoMemo )  );
		menu.getItems().add( item );
		
		item = new MenuItem( "DaoService" );
		item.setOnAction(  e -> managerGui.showView( EnumView.TestDaoService )  );
		menu.getItems().add( item );


		// Configuration initiale du menu
		configurerMenu( modelConnexion.getCompteActif() );

		// Le changement du compte connecté modifie automatiquement le menu
		modelConnexion.compteActifProperty().addListener( (obs) -> {
					Platform.runLater( () -> configurerMenu( modelConnexion.getCompteActif() ) );
				}
			); 
		
	}

	
	// Méthodes auxiliaires
	
	private void configurerMenu( Compte compteActif  ) {

		itemDeconnecter.setDisable(true);
		
		menuDonnees.setVisible(false);
		itemCategories.setVisible(false);
		itemComptes.setVisible(false);
		menuTests.setVisible(false);
		
		menuEtats.setVisible( false );
		
		if( compteActif != null ) {
			itemDeconnecter.setDisable(false);
			if( compteActif.isInRole( Roles.UTILISATEUR) ) {
				menuDonnees.setVisible(true);
				menuEtats.setVisible(true);
			}
			if( compteActif.isInRole( Roles.ADMINISTRATEUR ) ) {
				menuDonnees.setVisible(true);
				itemCategories.setVisible(true);
				itemComptes.setVisible(true);
				menuTests.setVisible(true);
			}
		}
	}
	
}
