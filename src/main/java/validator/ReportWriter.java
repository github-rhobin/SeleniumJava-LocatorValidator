package validator;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class ReportWriter {

	/**
	 * Writes a minimal HTML report for the provided results. Structure: className
	 * -> (fieldName -> status)
	 */
	public void writeHtml(File file, Map<String, Map<String, LocatorResult>> aggregateResults, String summaryNote)
			throws Exception {

		try (FileWriter writer = new FileWriter(file)) {
			writer.write("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
			writer.write("<title>Locator Validation Report</title>");
			writer.write("<style>");
			writer.write("body { font-family: Arial, sans-serif; margin:20px; font-size:12px; }"); 
			writer.write(".report-table { width:100%; border-collapse:collapse; margin-bottom:30px; font-size:11px; }");
			writer.write(".report-table th, .report-table td { border:1px solid #ccc; padding:4px 6px; text-align:left; }");
			writer.write(".report-table th { background:#f2f2f2; font-size:11px; }");
			//writer.write(".valid { background:#d4edda; }"); //green
			//writer.write(".multiple { background:#fff3cd; }"); //yellow
			//writer.write(".invalid { background:#f8d7da; }"); //red
			writer.write("</style></head><body>");


			writer.write("<h2>Locator Validation Report</h2>");
			writer.write("<p>Summary: " + escape(summaryNote) + "</p>");

			for (Map.Entry<String, Map<String, LocatorResult>> entry : aggregateResults.entrySet()) {
				String className = entry.getKey();
				writer.write("<h3>" + escape(className) + "</h3>");
				writer.write("<table class='report-table'>");
				writer.write("<thead><tr>");
				writer.write("<th>Locator Field</th>");
				writer.write("<th>Locator Type</th>");
				writer.write("<th>Locator Value</th>");
				writer.write("<th>Status</th>");
				writer.write("<th>Execution Result</th>");
				writer.write("</tr></thead><tbody>");

				for (LocatorResult result : entry.getValue().values()) {
					String cssClass = result.status.contains("Valid") ? "valid"
							: result.status.contains("Multiple") ? "multiple" : "invalid";

					writer.write("<tr class='" + cssClass + "'>");
					writer.write("<td>" + escape(result.fieldName) + "</td>");
					writer.write("<td>" + escape(result.locatorType) + "</td>");
					writer.write("<td>" + escape(result.locatorValue) + "</td>");
					writer.write("<td>" + escape(result.status) + "</td>");
					writer.write("<td>Count=" + result.count + "</td>");
					writer.write("</tr>");
				}

				writer.write("</tbody></table>");
			}

			writer.write("</body></html>");
		}
	}

// 🔑 Escape special characters for safe HTML rendering
	private String escape(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

}
