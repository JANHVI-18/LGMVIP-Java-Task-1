import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/currency-converter")
public class CurrencyConverterServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Serve HTML and JavaScript
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Currency Converter</title>");
        out.println("<style> body { font-family: Arial, sans-serif; margin: 20px; } "
                  + ".container { max-width: 400px; margin: auto; text-align: center; } "
                  + "input, select { margin: 10px; padding: 10px; width: 90%; } "
                  + "button { padding: 10px 20px; } </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>Currency Converter</h1>");
        out.println("<input type='number' id='amount' placeholder='Enter amount' required>");
        out.println("<select id='fromCurrency'>");
        out.println("<option value='USD'>USD</option>");
        out.println("<option value='INR'>INR</option>");
        out.println("<option value='EUR'>EUR</option>");
        out.println("</select>");
        out.println("<select id='toCurrency'>");
        out.println("<option value='INR'>INR</option>");
        out.println("<option value='USD'>USD</option>");
        out.println("<option value='EUR'>EUR</option>");
        out.println("</select>");
        out.println("<button onclick='convertCurrency()'>Convert</button>");
        out.println("<p id='result'>Converted Amount: </p>");
        out.println("<br><br>");

        // Java Applet (Demo Applet Code)
        out.println("<applet code='CurrencyApplet.class' width='300' height='200'>");
        out.println("</applet>");

        out.println("</div>");
        out.println("<script>");
        out.println("function convertCurrency() {");
        out.println("let fromCurrency = document.getElementById('fromCurrency').value;");
        out.println("let toCurrency = document.getElementById('toCurrency').value;");
        out.println("let amount = document.getElementById('amount').value;");
        out.println("let xhr = new XMLHttpRequest();");
        out.println("xhr.open('GET', '/currency-converter?from=' + fromCurrency + '&to=' + toCurrency + '&amount=' + amount, true);");
        out.println("xhr.onreadystatechange = function () {");
        out.println("if (xhr.readyState == 4 && xhr.status == 200) {");
        out.println("let response = JSON.parse(xhr.responseText);");
        out.println("document.getElementById('result').innerHTML = 'Converted Amount: ' + response.convertedAmount.toFixed(2);");
        out.println("}};");
        out.println("xhr.send();");
        out.println("}");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
        out.close();

        // Handle conversion logic
        String fromCurrency = request.getParameter("from");
        String toCurrency = request.getParameter("to");
        String amountStr = request.getParameter("amount");

        if (fromCurrency != null && toCurrency != null && amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);

                // Example API key and URL (replace with actual key and endpoint)
                String apiKey = "your_api_key"; // replace with a real API key
                String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + fromCurrency;

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                String inline = "";

                if (responseCode != 200) {
                    throw new RuntimeException("HttpResponseCode: " + responseCode);
                } else {
                    Scanner scanner = new Scanner(url.openStream());
                    while (scanner.hasNext()) {
                        inline += scanner.nextLine();
                    }
                    scanner.close();
                }

                // Parse JSON response
                JSONObject json = new JSONObject(inline);
                double exchangeRate = json.getJSONObject("conversion_rates").getDouble(toCurrency);

                double convertedAmount = amount * exchangeRate;

                // Return result in JSON format
                response.setContentType("application/json");
                PrintWriter jsonOut = response.getWriter();
                JSONObject result = new JSONObject();
                result.put("convertedAmount", convertedAmount);
                jsonOut.print(result);
                jsonOut.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
