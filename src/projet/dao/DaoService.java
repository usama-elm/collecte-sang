package projet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jfox.jdbc.UtilJdbc;
import projet.data.Service;


public class DaoService {

	
	// Champs

	@Inject
	private DataSource		dataSource;

	
	// Actions

	public int inserer( Service service ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs		= null;
		String				sql;
		
		try {
			cn = dataSource.getConnection();
			sql = "INSERT INTO service ( nom, anneecreation, flagSiege ) VALUES( ?, ?, ? ) ";
			stmt = cn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			stmt.setObject( 1, service.getNom() );
			stmt.setObject( 2, service.getAnneeCreation() );
			stmt.setObject( 3, service.getFlagSiege() );
			stmt.executeUpdate();

			// Récupère l'identifiant généré par le SGBD
			rs = stmt.getGeneratedKeys();
			rs.next();
			service.setId( rs.getObject( 1, Integer.class) );
			return service.getId();
	
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public void modifier( Service service ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "UPDATE service SET nom = ?, anneecreation = ?, flagsiege = ? WHERE idservice =  ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, service.getNom() );
			stmt.setObject( 2, service.getAnneeCreation() );
			stmt.setObject( 3, service.getFlagSiege() );
			stmt.setObject( 4, service.getId() );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}


	public void supprimer( int idService ) {

		Connection			cn 		= null;
		PreparedStatement	stmt 	= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "DELETE FROM service WHERE idservice = ? ";
			stmt = cn.prepareStatement( sql );
			stmt.setInt( 1, idService );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}

	
	public Service retrouver( int idService ) {

		Connection			cn 		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "SELECT * FROM service WHERE idservice = ?";
			stmt = cn.prepareStatement( sql );
			stmt.setInt(1, idService);
			rs = stmt.executeQuery();

			if ( rs.next() ) {
				return construireService( rs );
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public List<Service> listerTout() {

		Connection			cn 		= null;
		PreparedStatement	stmt 	= null;
		ResultSet 			rs		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "SELECT * FROM service ORDER BY nom";
			stmt = cn.prepareStatement( sql );
			rs = stmt.executeQuery();

			List<Service> services = new LinkedList<>();
			while (rs.next()) {
				services.add( construireService( rs ) );
			}
			return services;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}
	
	
	// Méthodes auxiliaires
	
	private Service construireService( ResultSet rs ) throws SQLException {
		Service service = new Service();
		service.setId( rs.getObject( "idservice", Integer.class ) );
		service.setNom( rs.getObject( "nom", String.class ) );
		service.setAnneeCreation( rs.getObject( "anneeCreation", Integer.class ) );
		service.setFlagSiege( rs.getObject( "flagsiege", Boolean.class ) );
		return service;
	}

}
