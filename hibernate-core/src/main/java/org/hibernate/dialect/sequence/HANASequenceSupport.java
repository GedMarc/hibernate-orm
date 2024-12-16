/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.dialect.sequence;

/**
 * Sequence support for {@link org.hibernate.dialect.HANADialect}.
 *
 * @author Gavin King
 */
public final class HANASequenceSupport extends NextvalSequenceSupport {

	public static final SequenceSupport INSTANCE = new HANASequenceSupport();

	@Override
	public String getFromDual() {
		return " from sys.dummy";
	}

	@Override
	public boolean sometimesNeedsStartingValue() {
		return true;
	}
}
