package projet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jfox.jdbc.UtilJdbc;
import projet.data.Memo;
import projet.data.Personne;


public class DaoMemo {

	
	// Champs

	@Inject
	private DataSource		dataSource;
	@Inject
	private DaoCategorie	daoCategorie;
	@Inject
	private DaoPersonne		daoPersonne;

	
	// Actions

	public int inserer( Memo memo ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "INSERT INTO memo ( titre, description, flagurgent, statut, effectif, budget, echeance, idcategorie ) VALUES( ?, ?, ?, ?, ?, ?, ?, ? ) ";
			stmt = cn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			stmt.setObject( 1, memo.getTitre() );
			stmt.setObject( 2, memo.getDescription() );
			stmt.setObject( 3, memo.getFlagUrgent() );
			stmt.setObject( 4, memo.getStatut() );
			stmt.setObject( 5, memo.getEffectif() );
			stmt.setObject( 6, memo.getBudget() );
			stmt.setObject( 7, memo.getEcheance() );
			if ( memo.getCategorie() == null ) {
				stmt.setObject( 8, null );
			} else {
				stmt.setObject( 8, memo.getCategorie().getId() );
			}   
			stmt.executeUpdate();

			// Récupère l'identifiant généré par le SGBD
			rs = stmt.getGeneratedKeys();
			rs.next();
			memo.setId( rs.getObject( 1, Integer.class) );
			
			insererConcerner(memo);
			return memo.getId();
	
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public void modifier( Memo memo ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "UPDATE memo SET titre = ?, description = ?, flagurgent = ?, statut = ?, effectif = ?, budget = ?, echeance = ?, idcategorie = ? WHERE idmemo =  ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, memo.getTitre() );
			stmt.setObject( 2, memo.getDescription() );
			stmt.setObject( 3, memo.getFlagUrgent() );
			stmt.setObject( 4, memo.getStatut() );
			stmt.setObject( 5, memo.getEffectif() );
			stmt.setObject( 6, memo.getBudget() );
			stmt.setObject( 7, memo.getEcheance() );
			if ( memo.getCategorie() == null ) {
				stmt.setObject( 8, null );
			} else {
				stmt.setObject( 8, memo.getCategorie().getId() );
			}   
			stmt.setObject( 9, memo.getId() );
			stmt.executeUpdate();
			
			supprimerConcerner( memo.getId() );
			insererConcerner( memo );

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}


	public void supprimer( int idMemo ) {

		Connection			cn 		= null;
		PreparedStatement	stmt 	= null;
		String				sql;

		try {
			supprimerConcerner(idMemo);
			
			cn = dataSource.getConnection();
			sql = "DELETE FROM memo WHERE idmemo = ? ";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, idMemo );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}

	
	public Memo retrouver( int idMemo ) {

		Connection			cn 		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "SELECT * FROM memo WHERE idmemo = ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject(1, idMemo);
			rs = stmt.executeQuery();

			if ( rs.next() ) {
				return construireMemo( rs, true );
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public List<Memo> listerTout() {

		Connection			cn 		= null;
		PreparedStatement	stmt 	= null;
		ResultSet 			rs		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "SELECT * FROM memo ORDER BY titre";
			stmt = cn.prepareStatement( sql );
			rs = stmt.executeQuery();

			List<Memo> memos = new ArrayList<>();
			while (rs.next()) {
				memos.add( construireMemo( rs, false ) );
			}
			return memos;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}
	
	
	// Méthodes auxiliaires
	
	private Memo construireMemo( ResultSet rs, boolean flagComplet ) throws SQLException {
		Memo memo = new Memo();
		memo.setId( rs.getObject( "idmemo", Integer.class ) );
		memo.setTitre( rs.getObject( "titre", String.class ) );
		memo.setDescription( rs.getObject( "description", String.class ) );
		memo.setFlagUrgent( rs.getObject( "flagurgent", Boolean.class ) );
		memo.setStatut( rs.getObject( "statut", Integer.class ) );
		memo.setEffectif( rs.getObject( "effectif", Integer.class ) );
		memo.setBudget( rs.getObject( "budget", Double.class ) );
		memo.setEcheance( rs.getObject( "echeance", LocalDate.class ) );
		if (flagComplet ) {
			Integer idCategorie = rs.getObject( "idcategorie", Integer.class );
			if ( idCategorie != null ) {
				memo.setCategorie( daoCategorie.retrouver( idCategorie ) );
			}
			memo.getPersonnes().setAll( daoPersonne.listerPourMemo( memo.getId() ) );
		}
		return memo;
	}


	private void supprimerConcerner( int idMemo ) {

		Connection			cn 		= null;
		PreparedStatement	stmt 	= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "DELETE FROM concerner WHERE idmemo = ? ";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, idMemo );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}


	private void insererConcerner( Memo memo ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "INSERT INTO concerner ( idmemo, idpersonne ) VALUES( ?, ? ) ";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, memo.getId() );
			for ( Personne personne : memo.getPersonnes() ) {
				stmt.setObject( 2, personne.getId() );
				stmt.executeUpdate();
			}
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}
	
}
