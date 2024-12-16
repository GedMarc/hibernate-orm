/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.orphan.one2one.fk.bidirectional;


/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class Employee {
	private Long id;
	private EmployeeInfo info;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EmployeeInfo getInfo() {
		return info;
	}

	public void setInfo(EmployeeInfo info) {
		this.info = info;
	}
}
