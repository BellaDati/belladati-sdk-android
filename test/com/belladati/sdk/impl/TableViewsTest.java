package com.belladati.sdk.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.testng.annotations.Test;

import com.belladati.sdk.impl.ViewImpl.UnknownViewTypeException;
import com.belladati.sdk.test.TestRequestHandler;
import com.belladati.sdk.view.TableView;
import com.belladati.sdk.view.TableView.Table;
import com.belladati.sdk.view.View;
import com.belladati.sdk.view.ViewType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Test
public class TableViewsTest extends SDKTest {
	private final String viewsUri = "/api/reports/views/";
	private final String id = "id";
	private final String name = "name";

	/** Table is loaded correctly. */
	public void loadViewTable() throws UnknownViewTypeException {
		View view = new TableViewImpl(service, builder.buildViewNode(id, name, "table"));
		int rows = 8;
		int columns = 12;
		server.register(viewsUri + id + "/table/bounds", builder.buildTableNode(rows, columns, 2, 2).toString());

		TableView.Table table = (Table) view.loadContent();

		assertEquals(table.getRowCount(), rows);
		assertEquals(table.getColumnCount(), columns);
		assertTrue(table.hasLeftHeader());
		assertTrue(table.hasTopHeader());

		server.assertRequestUris(viewsUri + id + "/table/bounds");

		assertEquals(view.toString(), name);
		assertTrue(table.toString().contains(id));
	}

	/** Table is loaded correctly via service. */
	public void loadViewTableFromService() {
		int rows = 8;
		int columns = 12;
		server.register(viewsUri + id + "/table/bounds", builder.buildTableNode(rows, columns, 2, 2).toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertEquals(table.getRowCount(), rows);
		assertEquals(table.getColumnCount(), columns);
		assertTrue(table.hasLeftHeader());
		assertTrue(table.hasTopHeader());

		server.assertRequestUris(viewsUri + id + "/table/bounds");
	}

	/** Table without left header. */
	public void tableZeroLeftHeader() {
		server.register(viewsUri + id + "/table/bounds", builder.buildTableNode(10, 12, 0, 2).toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertTrue(table.hasTopHeader());
		assertFalse(table.hasLeftHeader());
	}

	/** Table without top header. */
	public void tableZeroTopHeader() {
		server.register(viewsUri + id + "/table/bounds", builder.buildTableNode(10, 12, 2, 0).toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertFalse(table.hasTopHeader());
		assertTrue(table.hasLeftHeader());
	}

	/** Table view without rows attribute. */
	public void tableViewNoRows() {
		int columns = 12;
		ObjectNode tableNode = builder.buildTableNode(10, columns);
		tableNode.remove("rowsCount");
		server.register(viewsUri + id + "/table/bounds", tableNode.toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertEquals(table.getRowCount(), 0);
		assertEquals(table.getColumnCount(), columns);
	}

	/** Table view without columns attribute. */
	public void tableViewNoColumns() {
		int rows = 12;
		ObjectNode tableNode = builder.buildTableNode(rows, 10);
		tableNode.remove("columnsCount");
		server.register(viewsUri + id + "/table/bounds", tableNode.toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertEquals(table.getRowCount(), rows);
		assertEquals(table.getColumnCount(), 0);
	}

	/** Table view with non-numeric rows attribute. */
	public void tableViewRowsNotNumber() {
		int columns = 12;
		ObjectNode tableNode = builder.buildTableNode(10, columns);
		tableNode.put("rowsCount", "not a number");
		server.register(viewsUri + id + "/table/bounds", tableNode.toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertEquals(table.getRowCount(), 0);
		assertEquals(table.getColumnCount(), columns);
	}

	/** Table view with non-numeric columns attribute. */
	public void tableViewColumnsNotNumber() {
		int rows = 12;
		ObjectNode tableNode = builder.buildTableNode(rows, 10);
		tableNode.put("columnsCount", "not a number");
		server.register(viewsUri + id + "/table/bounds", tableNode.toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertEquals(table.getRowCount(), rows);
		assertEquals(table.getColumnCount(), 0);
	}

	/** Table view without top rows attribute. */
	public void tableViewNoTopRows() {
		ObjectNode tableNode = builder.buildTableNode(10, 12, 2, 2);
		tableNode.remove("topHeaderRowsCount");
		server.register(viewsUri + id + "/table/bounds", tableNode.toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertFalse(table.hasTopHeader());
		assertTrue(table.hasLeftHeader());
	}

	/** Table view without left columns attribute. */
	public void tableViewNoLeftColumns() {
		ObjectNode tableNode = builder.buildTableNode(12, 10, 2, 2);
		tableNode.remove("leftHeaderColumnsCount");
		server.register(viewsUri + id + "/table/bounds", tableNode.toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertFalse(table.hasLeftHeader());
		assertTrue(table.hasTopHeader());
	}

	/** Table view with non-numeric top rows attribute. */
	public void tableViewTopRowsNotNumber() {
		ObjectNode tableNode = builder.buildTableNode(10, 12, 2, 2);
		tableNode.put("topHeaderRowsCount", "not a number");
		server.register(viewsUri + id + "/table/bounds", tableNode.toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertFalse(table.hasTopHeader());
		assertTrue(table.hasLeftHeader());
	}

	/** Table view with non-numeric left columns attribute. */
	public void tableViewLeftColumnsNotNumber() {
		ObjectNode tableNode = builder.buildTableNode(12, 10, 2, 2);
		tableNode.put("leftHeaderColumnsCount", "not a number");
		server.register(viewsUri + id + "/table/bounds", tableNode.toString());

		TableView.Table table = (Table) service.loadViewContent(id, ViewType.TABLE);

		assertFalse(table.hasLeftHeader());
		assertTrue(table.hasTopHeader());
	}

	/** Left header first row less than zero. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableLeftHeaderBelowZero() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadLeftHeader(-1, 3);
	}

	/** Left header last row greater than number of rows. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableLeftHeaderAboveCount() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadLeftHeader(3, 10);
	}

	/** Left header first row greater than last row. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableLeftHeaderEndBeforeStart() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadLeftHeader(5, 3);
	}

	/** Top header first column less than zero. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableTopHeaderBelowZero() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadTopHeader(-1, 3);
	}

	/** Top header last column greater than column count. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableTopHeaderAboveCount() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadTopHeader(3, 10);
	}

	/** Top header first column greater than last column. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableTopHeaderEndBeforeStart() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadTopHeader(5, 3);
	}

	/** Data first row less than zero. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableDataRowBelowZero() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadData(-1, 3, 3, 3);
	}

	/** Data last row greater than number of rows. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableDataRowAboveCount() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadData(3, 10, 3, 3);
	}

	/** Data first row greater than last row. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableDataRowEndBeforeStart() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadData(5, 3, 3, 3);
	}

	/** Data first column less than zero. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableDataColumnBelowZero() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadData(3, 3, -1, 3);
	}

	/** Data last column greater than column count. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableDataColumnAboveCount() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadData(3, 3, 3, 10);
	}

	/** Data first column greater than last column. */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void tableDataColumnEndBeforeStart() {
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10));
		table.loadData(3, 3, 5, 3);
	}

	/** Left header is loaded correctly. */
	public void loadLeftHeader() {
		final int firstRow = 3;
		final int lastRow = 8;
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10, 2, 2));

		final JsonNode result = new ObjectMapper().createObjectNode().put("content", "some content");
		server.register(viewsUri + id + "/table/leftHeader", new TestRequestHandler() {
			@Override
			protected void handle(HttpHolder holder) throws IOException {
				Map<String, String> expectedParams = new HashMap<String, String>();
				expectedParams.put("rowsFrom", "" + firstRow);
				expectedParams.put("rowsTo", "" + lastRow);
				assertEquals(holder.getUrlParameters(), expectedParams);
				holder.response.setEntity(new StringEntity(result.toString()));
			}
		});

		assertEquals(table.loadLeftHeader(firstRow, lastRow), result);
		server.assertRequestUris(viewsUri + id + "/table/leftHeader");
	}

	/** Top header is loaded correctly. */
	public void loadTopHeader() {
		final int firstCol = 3;
		final int lastCol = 8;
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10, 2, 2));

		final JsonNode result = new ObjectMapper().createObjectNode().put("content", "some content");
		server.register(viewsUri + id + "/table/topHeader", new TestRequestHandler() {
			@Override
			protected void handle(HttpHolder holder) throws IOException {
				Map<String, String> expectedParams = new HashMap<String, String>();
				expectedParams.put("columnsFrom", "" + firstCol);
				expectedParams.put("columnsTo", "" + lastCol);
				assertEquals(holder.getUrlParameters(), expectedParams);
				holder.response.setEntity(new StringEntity(result.toString()));
			}
		});

		assertEquals(table.loadTopHeader(firstCol, lastCol), result);
		server.assertRequestUris(viewsUri + id + "/table/topHeader");
	}

	/** Data is loaded correctly. */
	public void loadData() {
		final int firstRow = 3;
		final int lastRow = 8;
		final int firstCol = 4;
		final int lastCol = 9;
		Table table = new TableViewImpl.TableImpl(service, id, builder.buildTableNode(10, 10, 2, 2));

		final JsonNode result = new ObjectMapper().createObjectNode().put("content", "some content");
		server.register(viewsUri + id + "/table/data", new TestRequestHandler() {
			@Override
			protected void handle(HttpHolder holder) throws IOException {
				Map<String, String> expectedParams = new HashMap<String, String>();
				expectedParams.put("rowsFrom", "" + firstRow);
				expectedParams.put("rowsTo", "" + lastRow);
				expectedParams.put("columnsFrom", "" + firstCol);
				expectedParams.put("columnsTo", "" + lastCol);
				assertEquals(holder.getUrlParameters(), expectedParams);
				holder.response.setEntity(new StringEntity(result.toString()));
			}
		});

		assertEquals(table.loadData(firstRow, lastRow, firstCol, lastCol), result);
		server.assertRequestUris(viewsUri + id + "/table/data");
	}
}
