/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.PreparedStatement;

/**
 * Interface to the object that prepares JDBC {@link PreparedStatement}s related to mutations
 * on behalf of a {@link JdbcCoordinator}.
 *
 * @author Steve Ebersole
 *
 * @see JdbcCoordinator#getMutationStatementPreparer()
 */
public interface MutationStatementPreparer {
	/**
	 * Prepare a statement.
	 *
	 * @param sql The SQL the statement to be prepared
	 * @param isCallable Whether to prepare as a callable statement.
	 *
	 * @return the prepared statement
	 */
	PreparedStatement prepareStatement(String sql, boolean isCallable);

	/**
	 * Prepare an INSERT statement, specifying how auto-generated (by the database) keys should be handled.  Really this
	 * is a boolean, but JDBC opted to define it instead using 2 int constants:
	 * <ul>
	 *     <li>{@link PreparedStatement#RETURN_GENERATED_KEYS}</li>
	 *     <li>{@link PreparedStatement#NO_GENERATED_KEYS}</li>
	 * </ul>
	 * <p>
	 * Generated keys are accessed afterwards via {@link PreparedStatement#getGeneratedKeys}
	 *
	 * @param sql The INSERT SQL
	 * @param autoGeneratedKeys The autoGeneratedKeys flag
	 *
	 * @return the prepared statement
	 *
	 * @see java.sql.Connection#prepareStatement(String, int)
	 */
	PreparedStatement prepareStatement(String sql, int autoGeneratedKeys);

	/**
	 * Prepare an INSERT statement, specifying columns which are auto-generated values to be returned.
	 * Generated keys are accessed afterwards via {@link PreparedStatement#getGeneratedKeys}
	 *
	 * @param sql - the SQL for the statement to be prepared
	 * @param columnNames The name of the columns to be returned in the generated keys result set.
	 *
	 * @return the prepared statement
	 *
	 * @see java.sql.Connection#prepareStatement(String, String[])
	 */
	PreparedStatement prepareStatement(String sql, String[] columnNames);
}
