/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.annotations.id.sequences.entities;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
@AttributeOverride(name = "longitude", column = @Column(name = "fld_longitude"))
public class Tower extends MilitaryBuilding {
}
