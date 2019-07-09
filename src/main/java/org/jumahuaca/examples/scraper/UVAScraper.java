package org.jumahuaca.examples.scraper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jumahuaca.examples.model.UVAExchange;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDateInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class UVAScraper {
	
	private String baseURL = "http://www.bcra.gov.ar/PublicacionesEstadisticas/Principales_variables_datos.asp?serie=7913&detalle=Unidad%20de%20Valor%20Adquisitivo%20(UVA)%A0(en%20pesos%20-con%20dos%20decimales-,%20base%2031.3.2016=14.05)";

	private String fromDateInputId = "fecha_desde";

	private String toDateInputId = "fecha_hasta";

	private String formName = "fecha";
	
	private int fromPeriodDay = 16;
	
	public List<UVAExchange> scrap(Integer year, Integer month)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);

		HtmlPage page = client.getPage(baseURL);

		LocalDate start = LocalDate.of(year, month, fromPeriodDay);
		
		LocalDate end = start.minusDays(1).plusMonths(1);

		List<HtmlForm> forms = page.getForms();
		HtmlForm form = null;
		for (HtmlForm htmlForm : forms) {
			if (htmlForm.getMethodAttribute().equals("POST") && htmlForm.getNameAttribute().equals(formName)) {
				form = htmlForm;
				break;
			}
		}
		HtmlDateInput fromDateInput = (HtmlDateInput) form.getInputsByName(fromDateInputId).iterator().next();
		fromDateInput.setValueAttribute(start.toString());

		HtmlDateInput toDateInput = (HtmlDateInput) form.getInputsByName(toDateInputId).iterator().next();
		toDateInput.setValueAttribute(end.toString());

		HtmlButton submit = (HtmlButton) form.getElementsByTagName("button").iterator().next();
		HtmlPage newPage = submit.click();

		DomElement tableDom = newPage.getElementsByTagName("table").get(0);

		Iterator<DomNode> iterator = tableDom.getChildren().iterator();

		List<UVAExchange> result = new ArrayList<UVAExchange>();
		while (iterator.hasNext()) {
			DomNode current = iterator.next();
			if (current instanceof HtmlTableBody) {
				HtmlTableRow row = ((HtmlTableBody) current).getRows().iterator().next();
				HtmlTableDataCell cell = (HtmlTableDataCell) row.getCell(0);
				HtmlTableDataCell cell2 = (HtmlTableDataCell) row.getCell(1);
				result.add(new UVAExchange(
						LocalDate.parse(cell.getTextContent(), DateTimeFormatter.ofPattern("dd/MM/yyyy")),
						BigDecimal.valueOf(Double.valueOf(cell2.getTextContent().trim().replaceAll(",", ".")))));

			}
		}

		client.close();

		return result;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public void setFromDateInputId(String fromDateInputId) {
		this.fromDateInputId = fromDateInputId;
	}

	public void setToDateInputId(String toDateInputId) {
		this.toDateInputId = toDateInputId;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setFromPeriodDay(int fromPeriodDay) {
		this.fromPeriodDay = fromPeriodDay;
	}

}
