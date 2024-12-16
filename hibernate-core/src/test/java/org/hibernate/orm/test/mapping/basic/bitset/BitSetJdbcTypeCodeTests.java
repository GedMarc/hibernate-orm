/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.mapping.basic.bitset;

import java.sql.Types;
import java.util.BitSet;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.metamodel.mapping.internal.BasicAttributeMapping;
import org.hibernate.metamodel.spi.MappingMetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link JdbcTypeCode}
 *
 * @author Steve Ebersole
 */
@DomainModel(annotatedClasses = BitSetJdbcTypeCodeTests.Product.class)
@SessionFactory
public class BitSetJdbcTypeCodeTests {

	@Test
	public void verifyMappings(SessionFactoryScope scope) {
		final MappingMetamodelImplementor mappingMetamodel = scope.getSessionFactory()
				.getRuntimeMetamodels()
				.getMappingMetamodel();

		final EntityPersister entityDescriptor = mappingMetamodel.findEntityDescriptor(Product.class);
		final BasicAttributeMapping attributeMapping = (BasicAttributeMapping) entityDescriptor.findAttributeMapping("bitSet");

		assertThat( attributeMapping.getJavaType().getJavaTypeClass(), equalTo( BitSet.class));

		assertThat(attributeMapping.getJdbcMapping().getValueConverter(), nullValue());

		assertThat(
				attributeMapping.getJdbcMapping().getJdbcType().getJdbcTypeCode(),
				is(Types.VARBINARY)
		);

		assertThat(attributeMapping.getJdbcMapping().getJavaTypeDescriptor().getJavaTypeClass(), equalTo(BitSet.class));

		scope.inTransaction(
				(session) -> {
					session.persist(new Product(1, BitSet.valueOf(BitSetHelper.BYTES)));
				}
		);

		scope.inSession(
				(session) -> {
					final Product product = session.get(Product.class, 1);
					assertThat(product.getBitSet(), equalTo(BitSet.valueOf(BitSetHelper.BYTES)));
				}
		);
	}

	@AfterEach
	public void dropData(SessionFactoryScope scope) {
		scope.inTransaction(
				(session) -> session.createMutationQuery("delete Product").executeUpdate()
		);
	}


	@Table(name = "Product")
	//tag::basic-bitset-example-jdbc-type-code[]
	@Entity(name = "Product")
	public static class Product {
		@Id
		private Integer id;

		@JdbcTypeCode(Types.VARBINARY)
		private BitSet bitSet;

		//Constructors, getters, and setters are omitted for brevity
		//end::basic-bitset-example-jdbc-type-code[]
		public Product() {
		}

		public Product(Number id, BitSet bitSet) {
			this.id = id.intValue();
			this.bitSet = bitSet;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public BitSet getBitSet() {
			return bitSet;
		}

		public void setBitSet(BitSet bitSet) {
			this.bitSet = bitSet;
		}
		//tag::basic-bitset-example-jdbc-type-code[]
	}
	//end::basic-bitset-example-jdbc-type-code[]
}
