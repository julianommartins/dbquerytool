package com.ibm.services.tools.wexws.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.services.tools.wexws.bo.QueryBO;
import com.ibm.services.tools.wexws.dao.DbDAO;
import com.ibm.services.tools.wexws.domain.SQLResponse;

/**
 * This code was created to BE a POC. Do not expect to find concise code every time, all the time we code in a hurry and just need to make it work to show to Executive line I DONT care if you dont
 * like this code or you believe that this code is Ugly!
 * 
 * @author julianom
 *
 */
@RestController
public class HtmlController {
	private final String htmlTemplate;
	private final String helpHtml;
	private final String historyHtml;
	private List<String> historyList;

	private final Object lock = new Object();

	public HtmlController() throws IOException {
		this.htmlTemplate = loadHtmlPage("/query.html");
		this.helpHtml = loadHtmlPage("/help.html");
		this.historyHtml = loadHtmlPage("/history.html");
		this.historyList = loadHistorylist();
	}

	private String loadHtmlPage(String filename) throws IOException {
		StringBuilder sb = new StringBuilder();

		InputStream is = getClass().getResourceAsStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}

	private String loadFile(String filename) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}

	@RequestMapping("/query.html")
	public String getQueryHtml(@RequestParam(value = "name", defaultValue = "World") String name) {
		return this.htmlTemplate.replaceAll("%wql%", "").replaceAll("%response%", "").replaceAll("%message%", "").replace("%restfulRequests%", "").replace("%profile%", "").replace("%token%", "").replace("%stemming%", "").replace("%spelling%", "");
	}

	@RequestMapping("/status.html")
	public String getStatusHtml(@RequestParam(value = "name", defaultValue = "World") String name) {
		return this.htmlTemplate.replaceAll("%wql%", "").replaceAll("%response%", "").replaceAll("%message%", "").replace("%restfulRequests%", "").replace("%profile%", "").replace("%token%", "").replace("%stemming%", "").replace("%spelling%", "");
	}

	@RequestMapping("/help.html")
	public String getHelpHtml(@RequestParam(value = "name", defaultValue = "World") String name) {
		return this.helpHtml;
	}

	@RequestMapping("/history.html")
	public String gethistoryHtml(@RequestParam(value = "filter", defaultValue = "") String filter) {

		StringBuilder sb = new StringBuilder();

		if (filter == null)
			filter = "";

		String[] filters = null;
		if (filter != null && filter.length() > 0) {
			filters = filter.split(" ");
		}

		int count = 0;
		for (String wql : this.historyList) {
			count++;
			try {
				if (matches(wql, filters)) {
					System.out.println("WQL---->" + wql);
					String wql2 = wql.substring(wql.indexOf("SELECT"), wql.length());
					String encoded = URLEncoder.encode(wql2, "UTF-8");
					sb.append("<tr>" + "<td>").append("<a href='/queryToHtml?env=dev&wql=").append(encoded).append("'>").append(count).append("</a><input type='checkbox' name='wqlId' value='").append(count).append("'></td>"
							// "<td>").append("<a href='/queryToHtml?env=systest&nlq=true&ontolection=true&smartCondition=true&wql=").append(encoded).append("'>").append(count).append("</a></td>"
							+ "<td><font size=\"1\">").append(wql).append("</font></td></tr>\n");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String decorated = decorate(sb.toString(), "SELECT", "FIELDS", "FACETS", "FROM", "TEXTSEARCH", "WHERE", "NUM", "SORTBY");

		return this.historyHtml.replace("%filter%", filter).replace("%history%", decorated);
	}

	private boolean matches(String wql, String[] filters) {
		if (filters == null)
			return true;
		int count = 0;
		for (String filter : filters) {
			if (wql.toLowerCase().indexOf(filter.trim().toLowerCase()) > -1) {
				count++;
			}
		}
		return (filters.length == count);
	}

	@RequestMapping("/deleteWQLs")
	public synchronized String deleteWQLs(@RequestParam(value = "wqlId") List<Integer> wqlIds, @RequestParam(value = "filterDel", defaultValue = "") String filter) {

		for (Integer id : wqlIds) {
			this.historyList.set(id - 1, "*deleted*");
		}

		updateHistory();

		return gethistoryHtml(filter);
	}

	private String decorate(String s, String... keys) {
		for (String k : keys) {
			s = s.replace(k + " ", "<div class=\"key-word\">" + k + "</div> ").replace(k + "\n", "<div class=\"key-word\">" + k + "</div> ");

		}
		return s;
	}

	private void saveHistory(String wql) {

		if (!wql.startsWith("--"))
			return;

		if (this.historyList.contains(wql))
			return;

		this.historyList.add(wql);

		synchronized (lock) {

			try {
				FileWriter fw = new FileWriter("history.data", true);
				fw.write("<wql>");
				fw.write(wql);
				fw.write("</wql>\n");
				fw.flush();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void updateHistory() {

		synchronized (lock) {
			try {
				FileWriter fw = new FileWriter("history.data");

				for (String wql : this.historyList) {

					if (!"*deleted*".equals(wql) && wql.trim().length() > 0) {

						fw.write("<wql>");
						fw.write(wql);
						fw.write("</wql>\n");

					}

				}
				fw.flush();
				fw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

			this.historyList = loadHistorylist();

		}

	}

	private List<String> loadHistorylist() {
		List<String> list = new ArrayList<String>();
		synchronized (lock) {
			try {
				String content = loadFile("history.data");

				for (String wql : content.split("</wql>")) {
					if (wql.trim().length() > 0) {
						wql = wql.replace("<wql>", "").replace("</wql>", "");
						while (wql.indexOf("\n\n") > -1) {
							wql = wql.replace("\n\n", "\n");
						}
						if (!list.contains(wql)) {
							list.add(wql);
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return list;
	}

	private String getHtml(ResultSet response) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sa = new StringBuilder();
		try {
			ResultSetMetaData rsmd;
			rsmd = response.getMetaData();
			int columnCount = rsmd.getColumnCount();
			sb.append("<table class=\"table table-striped table-bordered table-hover table-condensed\">");
			sb.append("<tr>");
			sb.append("<th title='Line Number'>").append("#").append("</th>");
			for (int i = 1; i <= columnCount; i++) {
				String name = rsmd.getColumnName(i);
				String colType = rsmd.getColumnTypeName(i);
				sb.append("<th title='" + colType + "'>").append(name).append("</th>");
			}
			sb.append("</tr>\n");
			int count = 0;
			while (response.next()) {
				count++;
				sb.append("<tr>");
				sb.append("<td>").append(count).append("</td>");
				for (int col = 1; col <= columnCount; col++) {
					sb.append("<td>").append(response.getString(col)).append("</td>");
				}
				sb.append("\r\n");
				sb.append("</tr>");
			}
			sb.append("</table>");

			sb.append("<h4>Metadata Details</h4>");
			sb.append("<table class=\"table table-striped table-bordered table-hover table-condensed\">");
			int numCols = rsmd.getColumnCount();
			for (int i = 1; i <= numCols; i++) {
				sb.append("<tr>");
				String tabName = rsmd.getTableName(i);
				String colName = tabName + "." + rsmd.getColumnName(i);
				sb.append("<td width=20%>" + colName + "</td>");
				String typeName = rsmd.getColumnTypeName(i);
				sb.append("<td>" + typeName);
				int precision = rsmd.getPrecision(i);
				if (precision > 0) {
					sb.append(" (" + precision + ")");
				}
				sb.append("</td>");
				sb.append("</tr>");
			} // for
			sb.append("</table>");

			sa.append("<h4>Total rows:" + count + "</h4>");

		} catch (SQLException e) {
			sb.append(e.getMessage());
			e.printStackTrace();
		}
		sa.append(sb.toString());
		return sa.toString();
	}

	@RequestMapping("/queryToHtml")
	public String queryToHtml(@RequestParam(value = "env", defaultValue = "dev") String environment, @RequestParam(value = "wql", defaultValue = "SELECT X FROM Y") String wql, @RequestParam(value = "token", defaultValue = "") String token) {

		wql = wql.replaceAll("select", "SELECT");
		saveHistory(wql);
		System.out.println("wql--->" + wql);
		if (wql.startsWith("--")) {
			wql = wql.substring(wql.indexOf("SELECT"), wql.length());
		}

		String cleanWQL = wql.replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " ").replaceAll(";", ""); // .replaceAll("\\.", "");
		SQLResponse wqlResponse = new SQLResponse();
		wqlResponse.setWql(wql);

		String outcome = this.htmlTemplate.replace("%wql%", wql);
		// if (!WexWsConstants.tokens.contains(token) || WexWsConstants.restrictedTokens.contains(token)) {
		// if (WexWsConstants.restrictedTokens.contains(token)) {
		// outcome = outcome.replace("%message%", "You dont have access to this mode.");
		// } else {
		// outcome = outcome.replace("%message%", "In order to perform a query, you must have a Valid token. Contact your project manager/Team leader to ask for one.");
		// }
		// outcome = outcome.replace("%response%", "");
		// outcome = outcome.replace("%token%", "");
		// return outcome;
		// }

		try {
			if (!cleanWQL.equalsIgnoreCase("SELECT X FROM Y")){
				QueryBO querybo = new QueryBO(new DbDAO(environment));
				ResultSet response = querybo.executeQueryDB(cleanWQL);

				String responseHtml = getHtml(response);

				outcome = outcome.replace("%response%", responseHtml);

				outcome = outcome.replace("%selected-" + environment + "%", "selected='selected'");

				String message = "";

				outcome = outcome.replace("%message%", message);

				if (token != null) {
					outcome = outcome.replace("%token%", token);
				}
			} else {
				outcome = outcome.replace("%message%", "");
				outcome = outcome.replace("%response%", "Please, type your query.");
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			outcome = outcome.replace("%selected-" + environment + "%", "selected='selected'");
			if (token != null) {
				outcome = outcome.replace("%token%", token);
			}
			outcome = outcome.replace("%message%", "Error while executing query.");
			outcome = outcome.replace("%response%", e.getMessage());
		}

		return outcome;
	}

}
