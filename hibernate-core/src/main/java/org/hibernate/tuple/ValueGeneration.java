/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.tuple;

import java.util.EnumSet;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.generator.Generator;
import org.hibernate.generator.InDatabaseGenerator;
import org.hibernate.generator.InMemoryGenerator;

/**
 * A value generator that can adapt to both Java value generation and database value generation.
 * <p>
 * This is an older API that predates {@link Generator}. It's often cleaner to implement either
 * {@link InMemoryGenerator} or {@link InDatabaseGenerator} directly.
 *
 * @author Steve Ebersole
 * @author Gavin King
 *
 * @see AnnotationValueGeneration
 *
 * @deprecated Replaced by {@link Generator}
 */
@Deprecated(since = "6", forRemoval = true)
public interface ValueGeneration extends InMemoryGenerator, InDatabaseGenerator {
	/**
	 * Specifies that the property value is generated:
	 * <ul>
	 * <li>{@linkplain GenerationTiming#INSERT when the entity is inserted},
	 * <li>{@linkplain GenerationTiming#UPDATE when the entity is updated},
	 * <li>{@linkplain GenerationTiming#ALWAYS whenever the entity is inserted or updated}, or
	 * <li>{@linkplain GenerationTiming#NEVER never}.
	 * </ul>
	 *
	 * @return The {@link GenerationTiming} specifying when the value is generated.
	 */
	GenerationTiming getGenerationTiming();

	@Override
	default EnumSet<EventType> getEventTypes() {
		return getGenerationTiming().getEquivalent().eventTypes();
	}

	/**
	 * Obtain the {@linkplain ValueGenerator Java value generator}, if the value is generated in
	 * Java, or return {@code null} if the value is generated by the database.
	 *
	 * @return The value generator
	 */
	ValueGenerator<?> getValueGenerator();

	@Override
	default Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
		return getValueGenerator().generateValue( (Session) session, owner, currentValue );
	}

	/**
	 * Determines if the column whose value is generated is included in the column list of the
	 * SQL {@code insert} or {@code update} statement, in the case where the value is generated
	 * by the database. For example, this method should return:
	 * <ul>
	 * <li>{@code true} if the value is generated by calling a SQL function like
	 *     {@code current_timestamp}, or
	 * <li>{@code false} if the value is generated by a trigger,
	 *     by {@link org.hibernate.annotations.GeneratedColumn generated always as}, or
	 *     using a {@linkplain org.hibernate.annotations.ColumnDefault column default value}.
	 * </ul>
	 * If the value is generated in Java, this method is not called, and so for backward
	 * compatibility with Hibernate 5 it is permitted to return any value. On the other hand,
	 * when a property value is generated in Java, the column certainly must be included in the
	 * column list, and so it's most correct for this method to return {@code true}!
	 *
	 * @return {@code true} if the column is included in the column list of the SQL statement.
	 */
	boolean referenceColumnInSql();

	/**
	 * A SQL expression indicating how to calculate the generated value when the property value
	 * is {@linkplain #generatedByDatabase() generated in the database} and the mapped column is
	 * {@linkplain #referenceColumnInSql() included in the SQL statement}. The SQL expression
	 * might be:
	 * <ul>
	 * <li>a function call like {@code current_timestamp} or {@code nextval('mysequence')}, or
	 * <li>a syntactic marker like {@code default}.
	 * </ul>
	 * When the property value is generated in Java, this method is not called, and its value is
	 * implicitly the string {@code "?"}, that is, a JDBC parameter to which the generated value
	 * is bound.
	 *
	 * @return The column value to be used in the generated SQL statement.
	 */
	String getDatabaseGeneratedReferencedColumnValue();

	/**
	 * A SQL expression indicating how to calculate the generated value when the property value
	 * is {@linkplain #generatedByDatabase() generated in the database} and the mapped column is
	 * {@linkplain #referenceColumnInSql() included in the SQL statement}. The SQL expression
	 * might be:
	 * <ul>
	 * <li>a function call like {@code current_timestamp} or {@code nextval('mysequence')}, or
	 * <li>a syntactic marker like {@code default}.
	 * </ul>
	 * When the property value is generated in Java, this method is not called, and its value is
	 * implicitly the string {@code "?"}, that is, a JDBC parameter to which the generated value
	 * is bound.
	 *
	 * @param dialect The {@linkplain Dialect SQL dialect}, allowing generation of an expression
	 *                in dialect-specific SQL.
	 * @return The column value to be used in the generated SQL statement.
	 */
	default String getDatabaseGeneratedReferencedColumnValue(Dialect dialect) {
		return getDatabaseGeneratedReferencedColumnValue();
	}

	@Override
	default String[] getReferencedColumnValues(Dialect dialect) {
		String columnValue = getDatabaseGeneratedReferencedColumnValue( dialect );
		return columnValue == null ? null : new String[] { columnValue };
	}

	@Override
	default boolean referenceColumnsInSql(Dialect dialect) {
		return referenceColumnInSql();
	}

	/**
	 * Determines if the property value is generated in Java, or by the database.
	 * <p>
	 * This default implementation returns true if the {@linkplain #getValueGenerator() Java
	 * value generator} is {@code null}.
	 *
	 * @return {@code true} if the value is generated by the database, or false if it is
	 *         generated in Java using a {@link ValueGenerator}.
	 */
	default boolean generatedByDatabase() {
		return getValueGenerator() == null;
	}

	/**
	 * Determines if the property value is written to JDBC as the argument of a JDBC {@code ?}
	 * parameter. This is the case when either:
	 * <ul>
	 * <li>the value is generated in Java, or
	 * <li>{@link #referenceColumnInSql()} is {@code true} and
	 *     {@link #getDatabaseGeneratedReferencedColumnValue()} returns {@code null}.
	 * </ul>
	 *
	 * @see org.hibernate.annotations.Generated#writable()
	 */
	default boolean writePropertyValue() {
		return !generatedByDatabase() // value generated in memory and then written as normal
			// current value of property of entity instance written completely as normal
			|| referenceColumnInSql() && getDatabaseGeneratedReferencedColumnValue()==null;
	}
}
