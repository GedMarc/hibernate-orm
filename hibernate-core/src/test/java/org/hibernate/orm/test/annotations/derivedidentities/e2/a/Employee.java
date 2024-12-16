/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.annotations.derivedidentities.e2.a;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

/**
 * @author Emmanuel Bernard
 */
@Entity
@IdClass(EmployeeId.class)
public class Employee {
	@Id String firstName;
	@Id String lastName;
}
