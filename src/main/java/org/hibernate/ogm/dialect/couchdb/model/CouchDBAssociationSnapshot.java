/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.hibernate.ogm.dialect.couchdb.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hibernate.ogm.datastore.spi.AssociationSnapshot;
import org.hibernate.ogm.datastore.spi.Tuple;
import org.hibernate.ogm.dialect.couchdb.json.CouchDBAssociation;
import org.hibernate.ogm.grid.AssociationKey;
import org.hibernate.ogm.grid.RowKey;
import org.hibernate.ogm.grid.impl.RowKeyBuilder;

/**
 * {@link AssociationSnapshot} implementation based on a {@link CouchDBAssociation} object as written to and retrieved
 * from the CouchDB server.
 *
 * @author Andrea Boriero <dreborier@gmail.com>
 * @author Gunnar Morling
 */
public class CouchDBAssociationSnapshot implements AssociationSnapshot {

	private final Map<RowKey, Map<String, Object>> rows = new HashMap<RowKey, Map<String, Object>>();

	public CouchDBAssociationSnapshot(CouchDBAssociation association, AssociationKey key) {
		for ( Map<String, Object> row : association.getRows() ) {
			Map<String, Object> rowKeyColumnValues = getRowKeyColumnValues( row, key );

			RowKey rowKey = new RowKeyBuilder()
					.tableName( key.getTable() )
					.addColumns( key.getRowKeyColumnNames() )
					.values( rowKeyColumnValues )
					.build();

			rows.put( rowKey, row );
		}
	}

	private static Map<String, Object> getRowKeyColumnValues(Map<String, Object> row, AssociationKey key) {
		Map<String, Object> rowKeyColumnValues = new HashMap<String, Object>();

		for ( String rowKeyColumnName : key.getRowKeyColumnNames() ) {
			rowKeyColumnValues.put( rowKeyColumnName, row.get( rowKeyColumnName ) );
		}

		return rowKeyColumnValues;
	}

	@Override
	public boolean containsKey(RowKey column) {
		return rows.containsKey( column );
	}

	@Override
	public Tuple get(RowKey column) {
		Map<String, Object> row = rows.get( column );
		return row != null ? new Tuple( new CouchDBTupleSnapshot( row ) ) : null;
	}

	@Override
	public int size() {
		return rows.size();
	}

	@Override
	public Set<RowKey> getRowKeys() {
		return rows.keySet();
	}
}
