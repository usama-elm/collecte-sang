SET search_path TO projet;


-- Supprime toutes les fonctions du schéma

DO $code$
DECLARE 
	r RECORD;
BEGIN
	FOR r IN
		SELECT 'DROP FUNCTION ' || ns.nspname || '.' || proname 
	       || '(' || oidvectortypes(proargtypes) || ')' AS sql
		FROM pg_proc INNER JOIN pg_namespace ns ON (pg_proc.pronamespace = ns.oid)
		WHERE ns.nspname = 'projet'  
	LOOP
		EXECUTE r.sql;
	END LOOP;
END;
$code$;


-- Fonctions


CREATE OR REPLACE FUNCTION compte_inserer( 
	p_pseudo		VARCHAR,
	p_motdepasse	VARCHAR,
	p_email			VARCHAR,
	OUT p_idCompte 	INT,
	p_roles			VARCHAR[]
) 
AS $code$
DECLARE	v_role VARCHAR;
BEGIN
    INSERT INTO compte ( pseudo, motdepasse, email )
    VALUES ( p_pseudo, p_motdepasse, p_email )
	RETURNING idcompte INTO p_idCompte; 

	FOREACH v_role IN ARRAY p_roles LOOP
		INSERT INTO role ( idcompte, role )
		VALUES ( p_idcompte, v_role );
	END LOOP;	
END;
$code$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION compte_modifier( 
	p_pseudo		VARCHAR,
	p_motdepasse	VARCHAR,
	p_email			VARCHAR,
	p_idCompte 		INT, 
	p_roles			VARCHAR[]
) 
RETURNS VOID 
AS $code$
DECLARE	v_role VARCHAR;
BEGIN
    UPDATE compte 
	SET pseudo = p_pseudo,
		motdepasse = p_motdepasse,
		email =	p_motdepasse
	WHERE idcompte = p_idcompte;
	
    DELETE FROM role WHERE idcompte = p_idcompte;

	FOREACH v_role IN ARRAY p_roles LOOP
		INSERT INTO role ( idcompte, role )
		VALUES ( p_idcompte, v_role );
	END LOOP;	
	
END;
$code$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION compte_supprimer( p_idCompte INT ) 
RETURNS VOID 
AS $code$
BEGIN
    DELETE FROM role WHERE idcompte = p_idcompte;
    DELETE FROM compte WHERE idcompte = p_idcompte;
END;
$code$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION compte_retrouver( p_idCompte INT ) 
RETURNS SETOF v_compte_avec_roles 
AS $code$
BEGIN
	RETURN QUERY
    SELECT * FROM v_compte_avec_roles WHERE idcompte = p_idcompte;
END;
$code$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION compte_lister_tout() 
RETURNS SETOF v_compte_avec_roles 
AS $code$
BEGIN
	RETURN QUERY
    SELECT * FROM v_compte_avec_roles ORDER BY pseudo;
END;
$code$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION compte_valider_authentification( p_pseudo VARCHAR, p_motdepasse VARCHAR ) 
RETURNS SETOF v_compte_avec_roles 
AS $code$
BEGIN
	RETURN QUERY
    SELECT *
	FROM v_compte_avec_roles
	WHERE pseudo = p_pseudo 
	  AND motdepasse = p_motdepasse;
END;
$code$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION compte_verifier_unicite_pseudo( 
	p_pseudo		VARCHAR,
	p_idCompte 		INT,
	OUT p_unicite	BOOLEAN	
) 
AS $code$
BEGIN
    SELECT COUNT(*) = 0 INTO p_unicite
    FROM compte
    WHERE pseudo = p_pseudo
      AND idcompte <> P_idcompte;
END;
$code$ LANGUAGE plpgsql;
