package com.belladati.sdk.impl;

import java.util.Locale;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import com.belladati.sdk.dataset.source.ImportIntervalUnit;
import com.belladati.sdk.test.JsonBuilder;
import com.belladati.sdk.test.RequestTrackingServer;
import com.belladati.sdk.view.ViewType;

/**
 * Superclass for all SDK test classes using a local test server.
 * 
 * @author Chris Hennigfeld
 */
public class SDKTest {

	/** the server; a new instance is created for every test method */
	protected RequestTrackingServer server;

	/** service instance to invoke test methods on */
	protected BellaDatiServiceImpl service;

	/** builds JSON objects */
	protected final JsonBuilder builder = new JsonBuilder();

	/** system default locale */
	private static final Locale DEFAULT_LOCALE = Locale.getDefault();

	@BeforeMethod(alwaysRun = true)
	protected void setupServer() throws Exception {
		server = new RequestTrackingServer();
		server.start();

		BellaDatiClient client = new BellaDatiClient(server.getHttpURL(), false);
		service = new BellaDatiServiceImpl(client, new TokenHolder("key", "secret"));
	}

	@AfterMethod(alwaysRun = true)
	protected void tearDownServer() throws Exception {
		server.stop();
	}

	/** resets the locale back to what it was */
	@AfterMethod(groups = "locale")
	protected void resetLocale() {
		Locale.setDefault(DEFAULT_LOCALE);
	}

	@DataProvider(name = "viewTypes")
	protected Object[][] viewTypeProvider() {
		return new Object[][] { { "chart", ViewType.CHART }, { "kpi", ViewType.KPI }, { "text", ViewType.TEXT },
			{ "table", ViewType.TABLE } };
	}

	@DataProvider(name = "jsonViewTypes")
	protected Object[][] jsonViewTypeProvider() {
		return new Object[][] { { "chart", ViewType.CHART }, { "kpi", ViewType.KPI }, { "text", ViewType.TEXT } };
	}

	@DataProvider(name = "unsupportedViewTypes")
	protected Object[][] unsupportedViewTypeProvider() {
		// we don't support tables yet
		return new Object[][] { { "unknown type" } };
	}

	@DataProvider(name = "intervalUnits")
	protected Object[][] intervalUnitsProvider() {
		return new Object[][] { new Object[] { "HOUR", ImportIntervalUnit.HOUR, 1, 60 },
			new Object[] { "HOUR2", ImportIntervalUnit.HOUR, 2, 120 }, new Object[] { "HOUR4", ImportIntervalUnit.HOUR, 4, 240 },
			new Object[] { "HOUR8", ImportIntervalUnit.HOUR, 8, 480 }, new Object[] { "DAY", ImportIntervalUnit.DAY, 1, 1440 },
			new Object[] { "DAY2", ImportIntervalUnit.DAY, 2, 2880 }, new Object[] { "WEEK", ImportIntervalUnit.WEEK, 1, 10080 },
			new Object[] { "WEEK2", ImportIntervalUnit.WEEK, 2, 20160 },
			new Object[] { "MONTH", ImportIntervalUnit.MONTH, 1, 44640 },
			new Object[] { "QUARTER", ImportIntervalUnit.QUARTER, 1, 133920 },
			new Object[] { "YEAR", ImportIntervalUnit.YEAR, 1, 525600 } };
	}
}
