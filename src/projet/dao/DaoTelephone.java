package projet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jfox.jdbc.UtilJdbc;
import projet.data.Personne;
import projet.data.Telephone;


public class DaoTelephone {

	
	// Champs

	@Inject
	private DataSource		dataSource;

	
	// Actions

	public void insererPourPersonne( Personne personne ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "INSERT INTO telephone ( idpersonne, libelle, numero ) VALUES (?,?,?)";
			stmt = cn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			for( Telephone telephone : personne.getTelephones() ) {
				stmt.setObject( 1, personne.getId() );
				stmt.setObject( 2, telephone.getLibelle() );
				stmt.setObject( 3, telephone.getNumero() );
				stmt.executeUpdate();

				// Récupère l'identifiant généré par le SGBD
				rs = stmt.getGeneratedKeys();
				rs.next();
				telephone.setId( rs.getObject( 1, Integer.class) );
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public void modifierPourPersonne( Personne personne ) {

		Connection			cn			= null;
		PreparedStatement	stmtDelete	= null;
		PreparedStatement	stmtInsert	= null;
		PreparedStatement	stmtUpdate	= null;
		ResultSet 			rs 			= null;
		String 				sql;

		try {
			cn = dataSource.getConnection();

			sql = "DELETE FROM telephone WHERE idtelephone = ?";
			stmtDelete = cn.prepareStatement( sql );
			for ( Telephone telephone : listerPourPersonne(personne) ) {
				if ( ! personne.getTelephones().contains( telephone ) ) {
					stmtDelete.setObject( 1, telephone.getId() );
					stmtDelete.executeUpdate();
				}
			}

			sql = "INSERT INTO telephone ( idpersonne, libelle, numero ) VALUES (?,?,?)";
			stmtInsert = cn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			sql = "UPDATE telephone SET idpersonne = ?, libelle = ?, numero = ? WHERE idtelephone = ?";
			stmtUpdate = cn.prepareStatement( sql );
			for( Telephone telephone : personne.getTelephones() ) {
				if ( telephone.getId() == null || telephone.getId() == 0 ) {
					stmtInsert.setObject( 1, personne.getId());
					stmtInsert.setObject( 2, telephone.getLibelle() );
					stmtInsert.setObject( 3, telephone.getNumero() );
					stmtInsert.executeUpdate();
					// Récupère l'identifiant généré par le SGBD
					rs = stmtInsert.getGeneratedKeys();
					rs.next();
					telephone.setId( rs.getObject( 1, Integer.class) );
					UtilJdbc.close( rs);
				} else {
					stmtUpdate.setObject( 1, personne.getId());
					stmtUpdate.setObject( 2, telephone.getLibelle() );
					stmtUpdate.setObject( 3, telephone.getNumero() );
					stmtUpdate.setObject( 4, telephone.getId());
					stmtUpdate.executeUpdate();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmtDelete, stmtInsert, stmtUpdate, cn );
		}
	}


	public void supprimerPourPersonne( int idPersonne ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String 				sql;

		try {
			cn = dataSource.getConnection();

			// Supprime les telephones
			sql = "DELETE FROM telephone  WHERE idpersonne = ? ";
			stmt = cn.prepareStatement(sql);
			stmt.setObject( 1, idPersonne );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close(  stmt, cn );
		}
	}


	public List<Telephone> listerPourPersonne( Personne personne ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			sql = "SELECT * FROM telephone WHERE idpersonne = ? ORDER BY libelle";
			stmt = cn.prepareStatement(sql);
			stmt.setObject( 1, personne.getId() );
			rs = stmt.executeQuery();

			List<Telephone> telephones = new ArrayList<>();
			while (rs.next()) {
				telephones.add( construireTelephone( rs, personne ) );
			}
			return telephones;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}
	
	
	// Méthodes auxiliaires
	
	private Telephone construireTelephone( ResultSet rs, Personne personne ) throws SQLException {
		Telephone telephone = new Telephone();
		telephone.setId(rs.getObject( "idtelephone", Integer.class ));
		telephone.setLibelle(rs.getObject( "libelle", String.class ));
		telephone.setNumero(rs.getObject( "numero", String.class ));
		return telephone;
	}

}
