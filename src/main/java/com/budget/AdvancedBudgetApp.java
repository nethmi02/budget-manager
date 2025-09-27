package com.budget;

import com.budget.database.DatabaseManager;
import com.budget.dao.CategoryDAO;
import com.budget.dao.ExpenseDAO;
import com.budget.dao.IncomeDAO;
import com.budget.dao.BudgetDAO;
import com.budget.model.Category;
import com.budget.model.Expense;
import com.budget.model.Income;
import com.budget.model.Budget;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Advanced Budget Manager with Modern UI and Charts
 */
public class AdvancedBudgetApp {
    
    private static CategoryDAO categoryDAO;
    private static ExpenseDAO expenseDAO;
    private static IncomeDAO incomeDAO;
    private static BudgetDAO budgetDAO;
    
    public static void main(String[] args) throws IOException {
        // Initialize database
        System.out.println("🚀 Starting Advanced Budget Manager...");
        DatabaseManager.initializeDatabase();
        System.out.println("✅ Database initialized successfully!");
        
        // Initialize DAOs
        categoryDAO = new CategoryDAO();
        expenseDAO = new ExpenseDAO();
        incomeDAO = new IncomeDAO();
        budgetDAO = new BudgetDAO();
        
        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Add handlers
        server.createContext("/", new ModernDashboardHandler());
        server.createContext("/api/summary", new AdvancedSummaryHandler());
        server.createContext("/api/chart-data", new ChartDataHandler());
        server.createContext("/transactions", new TransactionsPageHandler());
        server.createContext("/api/transactions", new TransactionsHandler());
        server.createContext("/add-transaction", new AddTransactionHandler());
        server.createContext("/budgets", new BudgetsPageHandler());
        server.createContext("/analytics", new AnalyticsPageHandler());
        server.createContext("/api/monthly-data", new MonthlyDataHandler());
        
        // Start server
        server.setExecutor(null);
        server.start();
        
        System.out.println("🌐 Advanced Budget Manager is running!");
        System.out.println("📱 Open your browser: http://localhost:8080");
        System.out.println("🛑 Press Ctrl+C to stop");
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n👋 Stopping Advanced Budget Manager...");
            DatabaseManager.closeConnection();
            server.stop(0);
            System.out.println("✅ Stopped gracefully");
        }));
    }
    
    static class ModernDashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = generateModernDashboard();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String generateModernDashboard() {
            // Get financial data
            LocalDate startDate = LocalDate.of(2000, 1, 1);
            LocalDate endDate = LocalDate.of(2099, 12, 31);
            BigDecimal totalIncome = incomeDAO.getTotalByDateRange(startDate, endDate);
            BigDecimal totalExpenses = expenseDAO.getTotalByDateRange(startDate, endDate);
            BigDecimal netBalance = totalIncome.subtract(totalExpenses);
            
            // Get recent transactions
            List<Expense> recentExpenses = expenseDAO.findAll();
            List<Income> recentIncomes = incomeDAO.findAll();
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>");
            html.append("<html lang='en'><head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            html.append("<title>💰 Advanced Budget Manager</title>");
            html.append("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
            html.append("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
            html.append("<style>");
            
            // Modern CSS
            html.append("""
                :root {
                    --primary: #667eea;
                    --primary-dark: #5a6fd8;
                    --success: #10b981;
                    --danger: #ef4444;
                    --warning: #f59e0b;
                    --info: #3b82f6;
                    --light: #f8fafc;
                    --dark: #1e293b;
                    --gray: #64748b;
                    --gray-light: #f1f5f9;
                    --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                    --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
                    --border-radius: 12px;
                }
                
                * { margin: 0; padding: 0; box-sizing: border-box; }
                
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    min-height: 100vh;
                    color: var(--dark);
                    line-height: 1.6;
                }
                
                .container {
                    max-width: 1400px;
                    margin: 0 auto;
                    padding: 20px;
                }
                
                .header {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 20px 30px;
                    margin-bottom: 30px;
                    box-shadow: var(--shadow);
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }
                
                .logo {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    font-size: 24px;
                    font-weight: 700;
                    color: var(--primary);
                }
                
                .nav-buttons {
                    display: flex;
                    gap: 12px;
                }
                
                .btn {
                    padding: 12px 24px;
                    border: none;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 14px;
                }
                
                .btn-primary {
                    background: var(--primary);
                    color: white;
                }
                
                .btn-primary:hover {
                    background: var(--primary-dark);
                    transform: translateY(-1px);
                }
                
                .btn-success {
                    background: var(--success);
                    color: white;
                }
                
                .btn-success:hover {
                    background: #059669;
                    transform: translateY(-1px);
                }
                
                .btn-outline {
                    background: transparent;
                    color: var(--gray);
                    border: 2px solid var(--gray-light);
                }
                
                .btn-outline:hover {
                    background: var(--gray-light);
                    color: var(--dark);
                }
                
                .stats-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                    gap: 20px;
                    margin-bottom: 30px;
                }
                
                .stat-card {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 24px;
                    box-shadow: var(--shadow);
                    transition: transform 0.2s;
                }
                
                .stat-card:hover {
                    transform: translateY(-2px);
                }
                
                .stat-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 16px;
                }
                
                .stat-title {
                    font-size: 14px;
                    color: var(--gray);
                    font-weight: 600;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                }
                
                .stat-icon {
                    width: 40px;
                    height: 40px;
                    border-radius: 10px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 18px;
                }
                
                .stat-icon.income { background: rgba(16, 185, 129, 0.1); color: var(--success); }
                .stat-icon.expense { background: rgba(239, 68, 68, 0.1); color: var(--danger); }
                .stat-icon.balance { background: rgba(59, 130, 246, 0.1); color: var(--info); }
                .stat-icon.budget { background: rgba(245, 158, 11, 0.1); color: var(--warning); }
                
                .stat-value {
                    font-size: 32px;
                    font-weight: 700;
                    margin-bottom: 8px;
                }
                
                .stat-value.positive { color: var(--success); }
                .stat-value.negative { color: var(--danger); }
                .stat-value.neutral { color: var(--info); }
                
                .stat-change {
                    font-size: 12px;
                    display: flex;
                    align-items: center;
                    gap: 4px;
                }
                
                .stat-change.up { color: var(--success); }
                .stat-change.down { color: var(--danger); }
                
                .main-grid {
                    display: grid;
                    grid-template-columns: 1fr 400px;
                    gap: 30px;
                    margin-bottom: 30px;
                }
                
                .chart-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 24px;
                    box-shadow: var(--shadow);
                }
                
                .section-title {
                    font-size: 18px;
                    font-weight: 700;
                    margin-bottom: 20px;
                    color: var(--dark);
                }
                
                .transactions-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 24px;
                    box-shadow: var(--shadow);
                }
                
                .transaction-item {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 12px 0;
                    border-bottom: 1px solid var(--gray-light);
                }
                
                .transaction-item:last-child {
                    border-bottom: none;
                }
                
                .transaction-info {
                    flex: 1;
                }
                
                .transaction-description {
                    font-weight: 600;
                    margin-bottom: 4px;
                }
                
                .transaction-category {
                    font-size: 12px;
                    color: var(--gray);
                }
                
                .transaction-amount {
                    font-weight: 700;
                    font-size: 16px;
                }
                
                .transaction-amount.income { color: var(--success); }
                .transaction-amount.expense { color: var(--danger); }
                
                .empty-state {
                    text-align: center;
                    padding: 40px 20px;
                    color: var(--gray);
                }
                
                .quick-actions {
                    display: flex;
                    gap: 12px;
                    margin-bottom: 20px;
                    flex-wrap: wrap;
                }
                
                @media (max-width: 768px) {
                    .main-grid {
                        grid-template-columns: 1fr;
                    }
                    
                    .stats-grid {
                        grid-template-columns: 1fr;
                    }
                    
                    .nav-buttons {
                        flex-direction: column;
                    }
                    
                    .header {
                        flex-direction: column;
                        gap: 20px;
                        text-align: center;
                    }
                }
                """);
            
            html.append("</style></head><body>");
            
            html.append("<div class='container'>");
            
            // Header
            html.append("<div class='header'>");
            html.append("<div class='logo'>");
            html.append("<i class='fas fa-wallet'></i>");
            html.append("Advanced Budget Manager");
            html.append("</div>");
            html.append("<div class='nav-buttons'>");
            html.append("<a href='/analytics' class='btn btn-outline'>");
            html.append("<i class='fas fa-chart-line'></i> Analytics");
            html.append("</a>");
            html.append("<a href='/budgets' class='btn btn-outline'>");
            html.append("<i class='fas fa-bullseye'></i> Budgets");
            html.append("</a>");
            html.append("<a href='/transactions' class='btn btn-outline'>");
            html.append("<i class='fas fa-list'></i> Transactions");
            html.append("</a>");
            html.append("</div>");
            html.append("</div>");
            
            // Stats Grid
            html.append("<div class='stats-grid'>");
            
            // Income Card
            html.append("<div class='stat-card'>");
            html.append("<div class='stat-header'>");
            html.append("<span class='stat-title'>Total Income</span>");
            html.append("<div class='stat-icon income'><i class='fas fa-arrow-up'></i></div>");
            html.append("</div>");
            html.append("<div class='stat-value positive'>$").append(String.format("%.2f", totalIncome.doubleValue())).append("</div>");
            html.append("<div class='stat-change up'><i class='fas fa-arrow-up'></i> +12.5% from last month</div>");
            html.append("</div>");
            
            // Expenses Card
            html.append("<div class='stat-card'>");
            html.append("<div class='stat-header'>");
            html.append("<span class='stat-title'>Total Expenses</span>");
            html.append("<div class='stat-icon expense'><i class='fas fa-arrow-down'></i></div>");
            html.append("</div>");
            html.append("<div class='stat-value negative'>$").append(String.format("%.2f", totalExpenses.doubleValue())).append("</div>");
            html.append("<div class='stat-change down'><i class='fas fa-arrow-down'></i> +5.2% from last month</div>");
            html.append("</div>");
            
            // Net Balance Card
            html.append("<div class='stat-card'>");
            html.append("<div class='stat-header'>");
            html.append("<span class='stat-title'>Net Balance</span>");
            html.append("<div class='stat-icon balance'><i class='fas fa-balance-scale'></i></div>");
            html.append("</div>");
            String balanceClass = netBalance.compareTo(BigDecimal.ZERO) >= 0 ? "positive" : "negative";
            html.append("<div class='stat-value ").append(balanceClass).append("'>$").append(String.format("%.2f", netBalance.doubleValue())).append("</div>");
            html.append("<div class='stat-change up'><i class='fas fa-arrow-up'></i> Healthy financial position</div>");
            html.append("</div>");
            
            // Savings Rate Card
            html.append("<div class='stat-card'>");
            html.append("<div class='stat-header'>");
            html.append("<span class='stat-title'>Savings Rate</span>");
            html.append("<div class='stat-icon budget'><i class='fas fa-piggy-bank'></i></div>");
            html.append("</div>");
            double savingsRate = totalIncome.doubleValue() > 0 ? 
                (netBalance.doubleValue() / totalIncome.doubleValue()) * 100 : 0;
            html.append("<div class='stat-value neutral'>").append(String.format("%.1f", savingsRate)).append("%</div>");
            html.append("<div class='stat-change up'><i class='fas fa-arrow-up'></i> Great saving habits!</div>");
            html.append("</div>");
            
            html.append("</div>");
            
            // Quick Actions
            html.append("<div class='quick-actions'>");
            html.append("<a href='/add-transaction?type=expense' class='btn btn-primary'>");
            html.append("<i class='fas fa-plus'></i> Add Expense");
            html.append("</a>");
            html.append("<a href='/add-transaction?type=income' class='btn btn-success'>");
            html.append("<i class='fas fa-plus'></i> Add Income");
            html.append("</a>");
            html.append("<button onclick='refreshDashboard()' class='btn btn-outline'>");
            html.append("<i class='fas fa-sync-alt'></i> Refresh");
            html.append("</button>");
            html.append("</div>");
            
            // Main Grid
            html.append("<div class='main-grid'>");
            
            // Chart Section
            html.append("<div class='chart-section'>");
            html.append("<h2 class='section-title'><i class='fas fa-chart-pie'></i> Spending Overview</h2>");
            html.append("<canvas id='expenseChart' width='400' height='200'></canvas>");
            html.append("</div>");
            
            // Recent Transactions
            html.append("<div class='transactions-section'>");
            html.append("<h2 class='section-title'><i class='fas fa-list'></i> Recent Activity</h2>");
            
            if (recentExpenses.isEmpty() && recentIncomes.isEmpty()) {
                html.append("<div class='empty-state'>");
                html.append("<i class='fas fa-receipt' style='font-size: 48px; margin-bottom: 16px; opacity: 0.3;'></i>");
                html.append("<p>No transactions yet</p>");
                html.append("<p style='font-size: 14px;'>Add your first expense or income to get started!</p>");
                html.append("</div>");
            } else {
                // Show recent transactions (mix of expenses and incomes)
                int count = 0;
                for (Expense expense : recentExpenses.stream().limit(3).collect(Collectors.toList())) {
                    html.append("<div class='transaction-item'>");
                    html.append("<div class='transaction-info'>");
                    html.append("<div class='transaction-description'>").append(expense.getDescription() != null ? expense.getDescription() : "Expense").append("</div>");
                    html.append("<div class='transaction-category'>Expense • ").append(expense.getExpenseDate()).append("</div>");
                    html.append("</div>");
                    html.append("<div class='transaction-amount expense'>-$").append(String.format("%.2f", expense.getAmount().doubleValue())).append("</div>");
                    html.append("</div>");
                    count++;
                }
                
                for (Income income : recentIncomes.stream().limit(3 - count).collect(Collectors.toList())) {
                    html.append("<div class='transaction-item'>");
                    html.append("<div class='transaction-info'>");
                    html.append("<div class='transaction-description'>").append(income.getDescription() != null ? income.getDescription() : "Income").append("</div>");
                    html.append("<div class='transaction-category'>Income • ").append(income.getIncomeDate()).append("</div>");
                    html.append("</div>");
                    html.append("<div class='transaction-amount income'>+$").append(String.format("%.2f", income.getAmount().doubleValue())).append("</div>");
                    html.append("</div>");
                }
            }
            
            html.append("</div>");
            html.append("</div>");
            
            html.append("</div>");
            
            // JavaScript for charts and interactivity
            html.append("<script>");
            html.append("""
                // Initialize expense chart
                const ctx = document.getElementById('expenseChart').getContext('2d');
                
                // Fetch chart data and create chart
                fetch('/api/chart-data')
                .then(response => response.json())
                .then(data => {
                    new Chart(ctx, {
                        type: 'doughnut',
                        data: {
                            labels: data.labels,
                            datasets: [{
                                data: data.values,
                                backgroundColor: [
                                    '#ef4444', '#f59e0b', '#10b981', '#3b82f6', 
                                    '#8b5cf6', '#f97316', '#06b6d4', '#84cc16'
                                ],
                                borderWidth: 0,
                                hoverOffset: 4
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: {
                                    position: 'bottom',
                                    labels: {
                                        usePointStyle: true,
                                        padding: 20
                                    }
                                }
                            }
                        }
                    });
                })
                .catch(() => {
                    // Show placeholder if no data
                    ctx.font = '16px Inter';
                    ctx.fillStyle = '#64748b';
                    ctx.textAlign = 'center';
                    ctx.fillText('Add expenses to see chart', ctx.canvas.width/2, ctx.canvas.height/2);
                });
                
                function refreshDashboard() {
                    location.reload();
                }
                """);
            html.append("</script>");
            
            html.append("</body></html>");
            
            return html.toString();
        }
    }
    
    static class ChartDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get expense data by category
            List<Category> expenseCategories = categoryDAO.findByType(Category.CategoryType.EXPENSE);
            StringBuilder json = new StringBuilder();
            json.append("{\"labels\":[");
            
            StringBuilder values = new StringBuilder();
            values.append("],\"values\":[");
            
            LocalDate startDate = LocalDate.of(2000, 1, 1);
            LocalDate endDate = LocalDate.of(2099, 12, 31);
            
            boolean first = true;
            for (Category category : expenseCategories) {
                BigDecimal total = expenseDAO.getTotalByCategoryAndDateRange(
                    category.getId(), startDate, endDate);
                
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    if (!first) {
                        json.append(",");
                        values.append(",");
                    }
                    json.append("\"").append(category.getName()).append("\"");
                    values.append(total.doubleValue());
                    first = false;
                }
            }
            
            json.append(values.toString()).append("]}");
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(json.toString().getBytes());
            os.close();
        }
    }
    
    static class AdvancedSummaryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LocalDate startDate = LocalDate.of(2000, 1, 1);
            LocalDate endDate = LocalDate.of(2099, 12, 31);
            BigDecimal totalIncome = incomeDAO.getTotalByDateRange(startDate, endDate);
            BigDecimal totalExpenses = expenseDAO.getTotalByDateRange(startDate, endDate);
            BigDecimal netBalance = totalIncome.subtract(totalExpenses);
            
            String json = String.format(
                "{\"totalIncome\":%.2f,\"totalExpenses\":%.2f,\"netBalance\":%.2f,\"savingsRate\":%.1f}",
                totalIncome.doubleValue(), totalExpenses.doubleValue(), 
                netBalance.doubleValue(), 
                totalIncome.doubleValue() > 0 ? (netBalance.doubleValue() / totalIncome.doubleValue()) * 100 : 0
            );
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        }
    }
    
    static class TransactionsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Expense> expenses = expenseDAO.findAll();
            List<Income> incomes = incomeDAO.findAll();
            
            StringBuilder json = new StringBuilder();
            json.append("{\"transactions\":[");
            
            boolean first = true;
            
            // Add expenses
            for (Expense expense : expenses) {
                if (!first) json.append(",");
                json.append("{");
                json.append("\"type\":\"expense\",");
                json.append("\"id\":").append(expense.getId()).append(",");
                json.append("\"amount\":").append(expense.getAmount()).append(",");
                json.append("\"description\":\"").append(expense.getDescription() != null ? expense.getDescription() : "").append("\",");
                json.append("\"date\":\"").append(expense.getExpenseDate()).append("\"");
                json.append("}");
                first = false;
            }
            
            // Add incomes
            for (Income income : incomes) {
                if (!first) json.append(",");
                json.append("{");
                json.append("\"type\":\"income\",");
                json.append("\"id\":").append(income.getId()).append(",");
                json.append("\"amount\":").append(income.getAmount()).append(",");
                json.append("\"description\":\"").append(income.getDescription() != null ? income.getDescription() : "").append("\",");
                json.append("\"date\":\"").append(income.getIncomeDate()).append("\"");
                json.append("}");
                first = false;
            }
            
            json.append("]}");
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(json.toString().getBytes());
            os.close();
        }
    }
    
    static class AddTransactionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                handleTransactionSubmission(exchange);
            } else {
                showAddTransactionForm(exchange);
            }
        }
        
        private void showAddTransactionForm(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String type = "expense"; // default
            if (query != null && query.contains("type=income")) {
                type = "income";
            }
            
            List<Category> categories = type.equals("income") ? 
                categoryDAO.findByType(Category.CategoryType.INCOME) :
                categoryDAO.findByType(Category.CategoryType.EXPENSE);
            
            String response = generateTransactionForm(type, categories);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String generateTransactionForm(String type, List<Category> categories) {
            boolean isIncome = "income".equals(type);
            String color = isIncome ? "var(--success)" : "var(--danger)";
            String icon = isIncome ? "fa-plus" : "fa-minus";
            String title = isIncome ? "Add Income" : "Add Expense";
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>");
            html.append("<html lang='en'><head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            html.append("<title>").append(title).append(" - Advanced Budget Manager</title>");
            html.append("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
            
            // Include the same modern CSS
            html.append("<style>");
            html.append("""
                :root {
                    --primary: #667eea;
                    --success: #10b981;
                    --danger: #ef4444;
                    --light: #f8fafc;
                    --dark: #1e293b;
                    --gray: #64748b;
                    --gray-light: #f1f5f9;
                    --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                    --border-radius: 12px;
                }
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    min-height: 100vh;
                    padding: 20px;
                }
                .container {
                    max-width: 600px;
                    margin: 0 auto;
                }
                .back-btn {
                    background: rgba(255, 255, 255, 0.2);
                    color: white;
                    padding: 12px 20px;
                    border-radius: 8px;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    margin-bottom: 20px;
                    transition: all 0.2s;
                }
                .back-btn:hover {
                    background: rgba(255, 255, 255, 0.3);
                }
                .form-card {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 40px;
                    box-shadow: var(--shadow);
                }
                .form-header {
                    text-align: center;
                    margin-bottom: 30px;
                }
                .form-icon {
                    width: 60px;
                    height: 60px;
                    border-radius: 15px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 24px;
                    margin: 0 auto 16px;
                }
                .form-title {
                    font-size: 24px;
                    font-weight: 700;
                    margin-bottom: 8px;
                }
                .form-subtitle {
                    color: var(--gray);
                }
                .form-group {
                    margin-bottom: 24px;
                }
                .form-label {
                    display: block;
                    font-weight: 600;
                    margin-bottom: 8px;
                    color: var(--dark);
                }
                .form-input, .form-select, .form-textarea {
                    width: 100%;
                    padding: 12px 16px;
                    border: 2px solid var(--gray-light);
                    border-radius: 8px;
                    font-size: 16px;
                    transition: border-color 0.2s;
                }
                .form-input:focus, .form-select:focus, .form-textarea:focus {
                    outline: none;
                    border-color: var(--primary);
                }
                .form-textarea {
                    resize: vertical;
                    min-height: 80px;
                }
                .submit-btn {
                    width: 100%;
                    padding: 16px;
                    border: none;
                    border-radius: 8px;
                    font-size: 16px;
                    font-weight: 600;
                    color: white;
                    cursor: pointer;
                    transition: all 0.2s;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: 8px;
                }
                .submit-btn:hover {
                    transform: translateY(-1px);
                }
                """);
            html.append("</style></head><body>");
            
            html.append("<div class='container'>");
            html.append("<a href='/' class='back-btn'>");
            html.append("<i class='fas fa-arrow-left'></i> Back to Dashboard");
            html.append("</a>");
            
            html.append("<div class='form-card'>");
            html.append("<div class='form-header'>");
            html.append("<div class='form-icon' style='background: rgba(").append(isIncome ? "16, 185, 129" : "239, 68, 68").append(", 0.1); color: ").append(color).append(";'>");
            html.append("<i class='fas ").append(icon).append("'></i>");
            html.append("</div>");
            html.append("<h1 class='form-title'>").append(title).append("</h1>");
            html.append("<p class='form-subtitle'>Track your ").append(type).append(" and stay on top of your finances</p>");
            html.append("</div>");
            
            html.append("<form method='POST'>");
            html.append("<input type='hidden' name='type' value='").append(type).append("'>");
            
            html.append("<div class='form-group'>");
            html.append("<label class='form-label'>Amount</label>");
            html.append("<input type='number' name='amount' class='form-input' step='0.01' min='0' placeholder='0.00' required>");
            html.append("</div>");
            
            html.append("<div class='form-group'>");
            html.append("<label class='form-label'>Category</label>");
            html.append("<select name='category' class='form-select' required>");
            html.append("<option value=''>Select a category</option>");
            for (Category category : categories) {
                html.append("<option value='").append(category.getId()).append("'>");
                html.append(category.getName()).append("</option>");
            }
            html.append("</select>");
            html.append("</div>");
            
            html.append("<div class='form-group'>");
            html.append("<label class='form-label'>Description</label>");
            html.append("<textarea name='description' class='form-textarea' placeholder='What was this ").append(type).append(" for?'></textarea>");
            html.append("</div>");
            
            html.append("<div class='form-group'>");
            html.append("<label class='form-label'>Date</label>");
            html.append("<input type='date' name='date' class='form-input' value='").append(LocalDate.now()).append("' required>");
            html.append("</div>");
            
            html.append("<button type='submit' class='submit-btn' style='background: ").append(color).append(";'>");
            html.append("<i class='fas ").append(icon).append("'></i> Add ").append(isIncome ? "Income" : "Expense");
            html.append("</button>");
            
            html.append("</form>");
            html.append("</div>");
            html.append("</div>");
            
            html.append("</body></html>");
            
            return html.toString();
        }
        
        private void handleTransactionSubmission(HttpExchange exchange) throws IOException {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> formData = parseFormData(body);
                
                String type = formData.get("type");
                boolean success = false;
                
                if ("income".equals(type)) {
                    Income income = new Income();
                    income.setCategoryId(Integer.parseInt(formData.get("category")));
                    income.setAmount(new BigDecimal(formData.get("amount")));
                    income.setDescription(formData.get("description"));
                    income.setIncomeDate(LocalDate.parse(formData.get("date")));
                    success = incomeDAO.create(income);
                } else {
                    Expense expense = new Expense();
                    expense.setCategoryId(Integer.parseInt(formData.get("category")));
                    expense.setAmount(new BigDecimal(formData.get("amount")));
                    expense.setDescription(formData.get("description"));
                    expense.setExpenseDate(LocalDate.parse(formData.get("date")));
                    success = expenseDAO.create(expense);
                }
                
                if (success) {
                    exchange.getResponseHeaders().set("Location", "/?success=" + type + "-added");
                    exchange.sendResponseHeaders(302, -1);
                } else {
                    exchange.getResponseHeaders().set("Location", "/add-transaction?type=" + type + "&error=save-failed");
                    exchange.sendResponseHeaders(302, -1);
                }
                
            } catch (Exception e) {
                exchange.getResponseHeaders().set("Location", "/add-transaction?error=invalid-data");
                exchange.sendResponseHeaders(302, -1);
            }
        }
    }
    
    static class BudgetsPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                handleBudgetSubmission(exchange);
            } else {
                showBudgetsPage(exchange);
            }
        }
        
        private void showBudgetsPage(HttpExchange exchange) throws IOException {
            String response = generateBudgetsPage();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String generateBudgetsPage() {
            List<Budget> budgets = budgetDAO.findAll();
            List<Category> expenseCategories = categoryDAO.findByType(Category.CategoryType.EXPENSE);
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>");
            html.append("<html lang='en'><head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            html.append("<title>💰 Budget Management - Advanced Budget Manager</title>");
            html.append("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
            html.append("<style>");
            
            // Modern CSS for budgets
            html.append("""
                :root {
                    --primary: #667eea;
                    --success: #10b981;
                    --danger: #ef4444;
                    --warning: #f59e0b;
                    --info: #3b82f6;
                    --dark: #1e293b;
                    --gray: #64748b;
                    --gray-light: #f1f5f9;
                    --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                    --border-radius: 12px;
                }
                
                * { margin: 0; padding: 0; box-sizing: border-box; }
                
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    min-height: 100vh;
                    color: var(--dark);
                    line-height: 1.6;
                }
                
                .container {
                    max-width: 1400px;
                    margin: 0 auto;
                    padding: 20px;
                }
                
                .header {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 20px 30px;
                    margin-bottom: 30px;
                    box-shadow: var(--shadow);
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }
                
                .page-title {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    font-size: 24px;
                    font-weight: 700;
                    color: var(--primary);
                }
                
                .back-btn {
                    background: var(--gray);
                    color: white;
                    padding: 12px 20px;
                    border-radius: 8px;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    transition: all 0.2s;
                }
                
                .back-btn:hover {
                    background: var(--dark);
                    transform: translateY(-1px);
                }
                
                .budget-actions {
                    display: flex;
                    gap: 12px;
                    margin-bottom: 30px;
                    flex-wrap: wrap;
                }
                
                .btn {
                    padding: 12px 24px;
                    border: none;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 14px;
                }
                
                .btn-primary {
                    background: var(--primary);
                    color: white;
                }
                
                .btn-primary:hover {
                    background: var(--primary);
                    transform: translateY(-1px);
                }
                
                .budgets-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
                    gap: 24px;
                    margin-bottom: 30px;
                }
                
                .budget-card {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 24px;
                    box-shadow: var(--shadow);
                    transition: transform 0.2s;
                }
                
                .budget-card:hover {
                    transform: translateY(-2px);
                }
                
                .budget-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 16px;
                }
                
                .budget-category {
                    font-size: 18px;
                    font-weight: 700;
                    color: var(--dark);
                }
                
                .budget-period {
                    background: var(--gray-light);
                    color: var(--gray);
                    padding: 4px 8px;
                    border-radius: 4px;
                    font-size: 12px;
                    font-weight: 600;
                }
                
                .budget-amounts {
                    margin-bottom: 16px;
                }
                
                .budget-amount-row {
                    display: flex;
                    justify-content: space-between;
                    margin-bottom: 8px;
                }
                
                .budget-label {
                    color: var(--gray);
                    font-size: 14px;
                }
                
                .budget-value {
                    font-weight: 600;
                    font-size: 14px;
                }
                
                .budget-value.spent {
                    color: var(--danger);
                }
                
                .budget-value.remaining {
                    color: var(--success);
                }
                
                .budget-progress {
                    margin-bottom: 16px;
                }
                
                .progress-bar {
                    width: 100%;
                    height: 8px;
                    background: var(--gray-light);
                    border-radius: 4px;
                    overflow: hidden;
                }
                
                .progress-fill {
                    height: 100%;
                    border-radius: 4px;
                    transition: width 0.3s;
                }
                
                .progress-fill.low {
                    background: var(--success);
                }
                
                .progress-fill.medium {
                    background: var(--warning);
                }
                
                .progress-fill.high {
                    background: var(--danger);
                }
                
                .budget-status {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 14px;
                    font-weight: 600;
                }
                
                .status-icon {
                    width: 20px;
                    height: 20px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 10px;
                }
                
                .status-icon.good {
                    background: var(--success);
                    color: white;
                }
                
                .status-icon.warning {
                    background: var(--warning);
                    color: white;
                }
                
                .status-icon.danger {
                    background: var(--danger);
                    color: white;
                }
                
                .create-budget-form {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 30px;
                    box-shadow: var(--shadow);
                    margin-bottom: 30px;
                }
                
                .form-title {
                    font-size: 20px;
                    font-weight: 700;
                    margin-bottom: 20px;
                    color: var(--dark);
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }
                
                .form-grid {
                    display: grid;
                    grid-template-columns: 1fr 1fr 1fr;
                    gap: 20px;
                    margin-bottom: 20px;
                }
                
                .form-group {
                    display: flex;
                    flex-direction: column;
                }
                
                .form-label {
                    font-weight: 600;
                    margin-bottom: 8px;
                    color: var(--dark);
                }
                
                .form-input, .form-select {
                    padding: 12px 16px;
                    border: 2px solid var(--gray-light);
                    border-radius: 8px;
                    font-size: 16px;
                    transition: border-color 0.2s;
                }
                
                .form-input:focus, .form-select:focus {
                    outline: none;
                    border-color: var(--primary);
                }
                
                .submit-btn {
                    background: var(--primary);
                    color: white;
                    padding: 14px 28px;
                    border: none;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }
                
                .submit-btn:hover {
                    background: var(--primary);
                    transform: translateY(-1px);
                }
                
                .empty-state {
                    text-align: center;
                    padding: 60px 20px;
                    color: var(--gray);
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    box-shadow: var(--shadow);
                }
                
                .empty-state i {
                    font-size: 64px;
                    margin-bottom: 20px;
                    opacity: 0.3;
                }
                
                @media (max-width: 768px) {
                    .form-grid {
                        grid-template-columns: 1fr;
                    }
                    
                    .header {
                        flex-direction: column;
                        gap: 20px;
                        text-align: center;
                    }
                }
                """);
            
            html.append("</style></head><body>");
            
            html.append("<div class='container'>");
            
            // Header
            html.append("<div class='header'>");
            html.append("<div class='page-title'>");
            html.append("<i class='fas fa-bullseye'></i>");
            html.append("Budget Management");
            html.append("</div>");
            html.append("<a href='/' class='back-btn'>");
            html.append("<i class='fas fa-arrow-left'></i> Back to Dashboard");
            html.append("</a>");
            html.append("</div>");
            
            // Create Budget Form
            html.append("<div class='create-budget-form'>");
            html.append("<h2 class='form-title'><i class='fas fa-plus-circle'></i> Create New Budget</h2>");
            html.append("<form method='POST'>");
            html.append("<div class='form-grid'>");
            
            html.append("<div class='form-group'>");
            html.append("<label class='form-label'>Category</label>");
            html.append("<select name='category' class='form-select' required>");
            html.append("<option value=''>Select Category</option>");
            for (Category category : expenseCategories) {
                html.append("<option value='").append(category.getId()).append("'>");
                html.append(category.getName()).append("</option>");
            }
            html.append("</select>");
            html.append("</div>");
            
            html.append("<div class='form-group'>");
            html.append("<label class='form-label'>Monthly Budget ($)</label>");
            html.append("<input type='number' name='amount' class='form-input' step='0.01' min='0' placeholder='500.00' required>");
            html.append("</div>");
            
            html.append("<div class='form-group'>");
            html.append("<label class='form-label'>Period</label>");
            html.append("<select name='period' class='form-select' required>");
            html.append("<option value='MONTHLY'>Monthly</option>");
            html.append("<option value='WEEKLY'>Weekly</option>");
            html.append("<option value='YEARLY'>Yearly</option>");
            html.append("</select>");
            html.append("</div>");
            
            html.append("</div>");
            
            html.append("<button type='submit' class='submit-btn'>");
            html.append("<i class='fas fa-plus'></i> Create Budget");
            html.append("</button>");
            
            html.append("</form>");
            html.append("</div>");
            
            // Budget Cards
            if (budgets.isEmpty()) {
                html.append("<div class='empty-state'>");
                html.append("<i class='fas fa-bullseye'></i>");
                html.append("<h3>No Budgets Created</h3>");
                html.append("<p>Create your first budget above to start tracking your spending goals!</p>");
                html.append("</div>");
            } else {
                html.append("<div class='budgets-grid'>");
                
                LocalDate startDate = LocalDate.of(2000, 1, 1);
                LocalDate endDate = LocalDate.of(2099, 12, 31);
                
                for (Budget budget : budgets) {
                    // Get category name
                    Category category = categoryDAO.findById(budget.getCategoryId()).orElse(null);
                    String categoryName = category != null ? category.getName() : "Unknown";
                    
                    // Calculate spent amount for this category
                    BigDecimal spent = expenseDAO.getTotalByCategoryAndDateRange(budget.getCategoryId(), startDate, endDate);
                    BigDecimal remaining = budget.getAmount().subtract(spent);
                    double percentage = budget.getAmount().doubleValue() > 0 ? 
                        (spent.doubleValue() / budget.getAmount().doubleValue()) * 100 : 0;
                    
                    // Determine status and color
                    String progressClass = "low";
                    String statusClass = "good";
                    String statusText = "On Track";
                    
                    if (percentage >= 90) {
                        progressClass = "high";
                        statusClass = "danger";
                        statusText = "Over Budget";
                    } else if (percentage >= 70) {
                        progressClass = "medium";
                        statusClass = "warning";
                        statusText = "Near Limit";
                    }
                    
                    html.append("<div class='budget-card'>");
                    html.append("<div class='budget-header'>");
                    html.append("<div class='budget-category'>").append(categoryName).append("</div>");
                    html.append("<div class='budget-period'>").append(budget.getPeriod() != null ? budget.getPeriod().toString() : "MONTHLY").append("</div>");
                    html.append("</div>");
                    
                    html.append("<div class='budget-amounts'>");
                    html.append("<div class='budget-amount-row'>");
                    html.append("<span class='budget-label'>Budget:</span>");
                    html.append("<span class='budget-value'>$").append(String.format("%.2f", budget.getAmount().doubleValue())).append("</span>");
                    html.append("</div>");
                    html.append("<div class='budget-amount-row'>");
                    html.append("<span class='budget-label'>Spent:</span>");
                    html.append("<span class='budget-value spent'>$").append(String.format("%.2f", spent.doubleValue())).append("</span>");
                    html.append("</div>");
                    html.append("<div class='budget-amount-row'>");
                    html.append("<span class='budget-label'>Remaining:</span>");
                    html.append("<span class='budget-value remaining'>$").append(String.format("%.2f", remaining.doubleValue())).append("</span>");
                    html.append("</div>");
                    html.append("</div>");
                    
                    html.append("<div class='budget-progress'>");
                    html.append("<div class='progress-bar'>");
                    html.append("<div class='progress-fill ").append(progressClass).append("' style='width: ").append(Math.min(100, percentage)).append("%;'></div>");
                    html.append("</div>");
                    html.append("</div>");
                    
                    html.append("<div class='budget-status'>");
                    html.append("<div class='status-icon ").append(statusClass).append("'>");
                    html.append("<i class='fas fa-").append(statusClass.equals("good") ? "check" : statusClass.equals("warning") ? "exclamation" : "times").append("'></i>");
                    html.append("</div>");
                    html.append("<span>").append(statusText).append(" (").append(String.format("%.0f", percentage)).append("%)</span>");
                    html.append("</div>");
                    
                    html.append("</div>");
                }
                
                html.append("</div>");
            }
            
            html.append("</div>");
            html.append("</body></html>");
            
            return html.toString();
        }
        
        private void handleBudgetSubmission(HttpExchange exchange) throws IOException {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> formData = parseFormData(body);
                
                Budget budget = new Budget();
                budget.setCategoryId(Integer.parseInt(formData.get("category")));
                budget.setAmount(new BigDecimal(formData.get("amount")));
                budget.setPeriod(Budget.Period.valueOf(formData.get("period")));
                budget.setStartDate(LocalDate.now());
                
                boolean success = budgetDAO.create(budget);
                
                if (success) {
                    exchange.getResponseHeaders().set("Location", "/budgets?success=budget-created");
                    exchange.sendResponseHeaders(302, -1);
                } else {
                    exchange.getResponseHeaders().set("Location", "/budgets?error=save-failed");
                    exchange.sendResponseHeaders(302, -1);
                }
                
            } catch (Exception e) {
                exchange.getResponseHeaders().set("Location", "/budgets?error=invalid-data");
                exchange.sendResponseHeaders(302, -1);
            }
        }
    }
    
    static class AnalyticsPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = generateAnalyticsPage();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String generateAnalyticsPage() {
            // Get comprehensive financial data
            LocalDate startDate = LocalDate.of(2000, 1, 1);
            LocalDate endDate = LocalDate.of(2099, 12, 31);
            
            BigDecimal totalIncome = incomeDAO.getTotalByDateRange(startDate, endDate);
            BigDecimal totalExpenses = expenseDAO.getTotalByDateRange(startDate, endDate);
            BigDecimal netBalance = totalIncome.subtract(totalExpenses);
            
            // Get monthly data for trends
            List<Expense> allExpenses = expenseDAO.findAll();
            List<Income> allIncomes = incomeDAO.findAll();
            List<Category> expenseCategories = categoryDAO.findByType(Category.CategoryType.EXPENSE);
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>");
            html.append("<html lang='en'><head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            html.append("<title>📊 Analytics - Advanced Budget Manager</title>");
            html.append("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
            html.append("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
            html.append("<style>");
            
            // Include modern CSS
            html.append("""
                :root {
                    --primary: #667eea;
                    --primary-dark: #5a6fd8;
                    --success: #10b981;
                    --danger: #ef4444;
                    --warning: #f59e0b;
                    --info: #3b82f6;
                    --light: #f8fafc;
                    --dark: #1e293b;
                    --gray: #64748b;
                    --gray-light: #f1f5f9;
                    --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                    --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
                    --border-radius: 12px;
                }
                
                * { margin: 0; padding: 0; box-sizing: border-box; }
                
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    min-height: 100vh;
                    color: var(--dark);
                    line-height: 1.6;
                }
                
                .container {
                    max-width: 1400px;
                    margin: 0 auto;
                    padding: 20px;
                }
                
                .header {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 20px 30px;
                    margin-bottom: 30px;
                    box-shadow: var(--shadow);
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }
                
                .analytics-title {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    font-size: 24px;
                    font-weight: 700;
                    color: var(--primary);
                }
                
                .back-btn {
                    background: var(--gray);
                    color: white;
                    padding: 12px 20px;
                    border-radius: 8px;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    transition: all 0.2s;
                }
                
                .back-btn:hover {
                    background: var(--dark);
                    transform: translateY(-1px);
                }
                
                .analytics-grid {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 30px;
                    margin-bottom: 30px;
                }
                
                .chart-card {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 24px;
                    box-shadow: var(--shadow);
                }
                
                .chart-title {
                    font-size: 18px;
                    font-weight: 700;
                    margin-bottom: 20px;
                    color: var(--dark);
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }
                
                .insights-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 24px;
                    box-shadow: var(--shadow);
                    margin-bottom: 30px;
                }
                
                .insights-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                    gap: 20px;
                }
                
                .insight-card {
                    background: var(--gray-light);
                    border-radius: 8px;
                    padding: 20px;
                    border-left: 4px solid var(--primary);
                }
                
                .insight-card.warning {
                    border-left-color: var(--warning);
                }
                
                .insight-card.success {
                    border-left-color: var(--success);
                }
                
                .insight-card.danger {
                    border-left-color: var(--danger);
                }
                
                .insight-title {
                    font-weight: 600;
                    margin-bottom: 8px;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }
                
                .insight-description {
                    color: var(--gray);
                    font-size: 14px;
                }
                
                .trend-indicators {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                    gap: 20px;
                    margin-bottom: 30px;
                }
                
                .trend-card {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 20px;
                    box-shadow: var(--shadow);
                    text-align: center;
                }
                
                .trend-icon {
                    width: 50px;
                    height: 50px;
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 0 auto 12px;
                    font-size: 20px;
                }
                
                .trend-value {
                    font-size: 24px;
                    font-weight: 700;
                    margin-bottom: 4px;
                }
                
                .trend-label {
                    color: var(--gray);
                    font-size: 14px;
                }
                
                @media (max-width: 768px) {
                    .analytics-grid {
                        grid-template-columns: 1fr;
                    }
                    
                    .header {
                        flex-direction: column;
                        gap: 20px;
                        text-align: center;
                    }
                }
                """);
            
            html.append("</style></head><body>");
            
            html.append("<div class='container'>");
            
            // Header
            html.append("<div class='header'>");
            html.append("<div class='analytics-title'>");
            html.append("<i class='fas fa-chart-line'></i>");
            html.append("Financial Analytics");
            html.append("</div>");
            html.append("<a href='/' class='back-btn'>");
            html.append("<i class='fas fa-arrow-left'></i> Back to Dashboard");
            html.append("</a>");
            html.append("</div>");
            
            // Trend Indicators
            html.append("<div class='trend-indicators'>");
            
            // Average Daily Spending
            double avgDailySpending = allExpenses.size() > 0 ? totalExpenses.doubleValue() / Math.max(allExpenses.size(), 1) : 0;
            html.append("<div class='trend-card'>");
            html.append("<div class='trend-icon' style='background: rgba(239, 68, 68, 0.1); color: var(--danger);'>");
            html.append("<i class='fas fa-calendar-day'></i>");
            html.append("</div>");
            html.append("<div class='trend-value'>$").append(String.format("%.2f", avgDailySpending)).append("</div>");
            html.append("<div class='trend-label'>Avg Daily Spending</div>");
            html.append("</div>");
            
            // Largest Expense Category
            String largestCategory = "None";
            double largestAmount = 0;
            for (Category category : expenseCategories) {
                BigDecimal categoryTotal = expenseDAO.getTotalByCategoryAndDateRange(category.getId(), startDate, endDate);
                if (categoryTotal.doubleValue() > largestAmount) {
                    largestAmount = categoryTotal.doubleValue();
                    largestCategory = category.getName();
                }
            }
            
            html.append("<div class='trend-card'>");
            html.append("<div class='trend-icon' style='background: rgba(245, 158, 11, 0.1); color: var(--warning);'>");
            html.append("<i class='fas fa-crown'></i>");
            html.append("</div>");
            html.append("<div class='trend-value'>").append(largestCategory).append("</div>");
            html.append("<div class='trend-label'>Top Expense Category</div>");
            html.append("</div>");
            
            // Financial Health Score
            double healthScore = totalIncome.doubleValue() > 0 ? 
                Math.min(100, Math.max(0, (netBalance.doubleValue() / totalIncome.doubleValue()) * 100 + 50)) : 50;
            html.append("<div class='trend-card'>");
            html.append("<div class='trend-icon' style='background: rgba(16, 185, 129, 0.1); color: var(--success);'>");
            html.append("<i class='fas fa-heart'></i>");
            html.append("</div>");
            html.append("<div class='trend-value'>").append(String.format("%.0f", healthScore)).append("%</div>");
            html.append("<div class='trend-label'>Financial Health</div>");
            html.append("</div>");
            
            // Transaction Count
            int totalTransactions = allExpenses.size() + allIncomes.size();
            html.append("<div class='trend-card'>");
            html.append("<div class='trend-icon' style='background: rgba(59, 130, 246, 0.1); color: var(--info);'>");
            html.append("<i class='fas fa-list'></i>");
            html.append("</div>");
            html.append("<div class='trend-value'>").append(totalTransactions).append("</div>");
            html.append("<div class='trend-label'>Total Transactions</div>");
            html.append("</div>");
            
            html.append("</div>");
            
            // Charts Grid
            html.append("<div class='analytics-grid'>");
            
            // Monthly Trend Chart
            html.append("<div class='chart-card'>");
            html.append("<h3 class='chart-title'><i class='fas fa-line-chart'></i> Spending Trends</h3>");
            html.append("<canvas id='trendChart' width='400' height='200'></canvas>");
            html.append("</div>");
            
            // Category Breakdown
            html.append("<div class='chart-card'>");
            html.append("<h3 class='chart-title'><i class='fas fa-pie-chart'></i> Category Breakdown</h3>");
            html.append("<canvas id='categoryChart' width='400' height='200'></canvas>");
            html.append("</div>");
            
            html.append("</div>");
            
            // Financial Insights
            html.append("<div class='insights-section'>");
            html.append("<h2 class='chart-title'><i class='fas fa-lightbulb'></i> Smart Insights</h2>");
            html.append("<div class='insights-grid'>");
            
            // Generate insights based on data
            if (totalExpenses.compareTo(totalIncome) > 0) {
                html.append("<div class='insight-card danger'>");
                html.append("<div class='insight-title'><i class='fas fa-exclamation-triangle'></i> Spending Alert</div>");
                html.append("<div class='insight-description'>Your expenses exceed your income. Consider reviewing your spending habits and finding areas to cut back.</div>");
                html.append("</div>");
            }
            
            if (healthScore >= 70) {
                html.append("<div class='insight-card success'>");
                html.append("<div class='insight-title'><i class='fas fa-check-circle'></i> Great Financial Health</div>");
                html.append("<div class='insight-description'>You're maintaining excellent financial habits! Keep up the good work with your savings and spending control.</div>");
                html.append("</div>");
            }
            
            if (avgDailySpending > 50) {
                html.append("<div class='insight-card warning'>");
                html.append("<div class='insight-title'><i class='fas fa-info-circle'></i> Daily Spending Pattern</div>");
                html.append("<div class='insight-description'>Your average daily spending is $").append(String.format("%.2f", avgDailySpending)).append(". Consider setting daily spending limits to improve control.</div>");
                html.append("</div>");
            }
            
            html.append("<div class='insight-card'>");
            html.append("<div class='insight-title'><i class='fas fa-target'></i> Savings Opportunity</div>");
            html.append("<div class='insight-description'>Focus on your largest expense category (").append(largestCategory).append(") to find the biggest savings opportunities.</div>");
            html.append("</div>");
            
            html.append("</div>");
            html.append("</div>");
            
            html.append("</div>");
            
            // JavaScript for charts
            html.append("<script>");
            html.append("""
                // Trend Chart
                const trendCtx = document.getElementById('trendChart').getContext('2d');
                new Chart(trendCtx, {
                    type: 'line',
                    data: {
                        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                        datasets: [{
                            label: 'Expenses',
                            data: [1200, 1900, 800, 1500, 2000, 1700],
                            borderColor: '#ef4444',
                            backgroundColor: 'rgba(239, 68, 68, 0.1)',
                            tension: 0.4,
                            fill: true
                        }, {
                            label: 'Income',
                            data: [2000, 2200, 1800, 2100, 2300, 2000],
                            borderColor: '#10b981',
                            backgroundColor: 'rgba(16, 185, 129, 0.1)',
                            tension: 0.4,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'top'
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return '$' + value;
                                    }
                                }
                            }
                        }
                    }
                });
                
                // Category Chart
                const categoryCtx = document.getElementById('categoryChart').getContext('2d');
                fetch('/api/chart-data')
                .then(response => response.json())
                .then(data => {
                    new Chart(categoryCtx, {
                        type: 'doughnut',
                        data: {
                            labels: data.labels,
                            datasets: [{
                                data: data.values,
                                backgroundColor: [
                                    '#ef4444', '#f59e0b', '#10b981', '#3b82f6', 
                                    '#8b5cf6', '#f97316', '#06b6d4', '#84cc16'
                                ],
                                borderWidth: 0,
                                hoverOffset: 4
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: {
                                    position: 'bottom',
                                    labels: {
                                        usePointStyle: true,
                                        padding: 20
                                    }
                                }
                            }
                        }
                    });
                })
                .catch(() => {
                    categoryCtx.font = '16px Inter';
                    categoryCtx.fillStyle = '#64748b';
                    categoryCtx.textAlign = 'center';
                    categoryCtx.fillText('Add expenses to see breakdown', categoryCtx.canvas.width/2, categoryCtx.canvas.height/2);
                });
                """);
            html.append("</script>");
            
            html.append("</body></html>");
            
            return html.toString();
        }
    }
    
    static class TransactionsPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = generateTransactionsPage(exchange);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String generateTransactionsPage(HttpExchange exchange) {
            // Parse query parameters for filtering
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQueryParams(query != null ? query : "");
            
            String searchTerm = params.getOrDefault("search", "");
            String categoryFilter = params.getOrDefault("category", "");
            String typeFilter = params.getOrDefault("type", "");
            String dateFrom = params.getOrDefault("dateFrom", "");
            String dateTo = params.getOrDefault("dateTo", "");
            
            // Get filtered data
            List<Expense> expenses = expenseDAO.findAll();
            List<Income> incomes = incomeDAO.findAll();
            List<Category> categories = categoryDAO.findAll();
            
            // Apply filters
            expenses = filterExpenses(expenses, searchTerm, categoryFilter, dateFrom, dateTo);
            incomes = filterIncomes(incomes, searchTerm, categoryFilter, dateFrom, dateTo);
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>");
            html.append("<html lang='en'><head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            html.append("<title>📋 Transaction Management - Advanced Budget Manager</title>");
            html.append("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
            html.append("<style>");
            
            // Advanced CSS for transaction management
            html.append("""
                :root {
                    --primary: #667eea;
                    --success: #10b981;
                    --danger: #ef4444;
                    --warning: #f59e0b;
                    --info: #3b82f6;
                    --dark: #1e293b;
                    --gray: #64748b;
                    --gray-light: #f1f5f9;
                    --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                    --border-radius: 12px;
                }
                
                * { margin: 0; padding: 0; box-sizing: border-box; }
                
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    min-height: 100vh;
                    color: var(--dark);
                    line-height: 1.6;
                }
                
                .container {
                    max-width: 1400px;
                    margin: 0 auto;
                    padding: 20px;
                }
                
                .header {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 20px 30px;
                    margin-bottom: 30px;
                    box-shadow: var(--shadow);
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }
                
                .page-title {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    font-size: 24px;
                    font-weight: 700;
                    color: var(--primary);
                }
                
                .back-btn {
                    background: var(--gray);
                    color: white;
                    padding: 12px 20px;
                    border-radius: 8px;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    transition: all 0.2s;
                }
                
                .back-btn:hover {
                    background: var(--dark);
                    transform: translateY(-1px);
                }
                
                .filters-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 24px;
                    box-shadow: var(--shadow);
                    margin-bottom: 30px;
                }
                
                .filters-title {
                    font-size: 18px;
                    font-weight: 700;
                    margin-bottom: 20px;
                    color: var(--dark);
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }
                
                .filters-grid {
                    display: grid;
                    grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
                    gap: 16px;
                    margin-bottom: 20px;
                }
                
                .filter-group {
                    display: flex;
                    flex-direction: column;
                }
                
                .filter-label {
                    font-weight: 600;
                    margin-bottom: 6px;
                    color: var(--dark);
                    font-size: 14px;
                }
                
                .filter-input, .filter-select {
                    padding: 10px 12px;
                    border: 2px solid var(--gray-light);
                    border-radius: 8px;
                    font-size: 14px;
                    transition: border-color 0.2s;
                }
                
                .filter-input:focus, .filter-select:focus {
                    outline: none;
                    border-color: var(--primary);
                }
                
                .filter-actions {
                    display: flex;
                    gap: 12px;
                    flex-wrap: wrap;
                }
                
                .btn {
                    padding: 10px 20px;
                    border: none;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 14px;
                }
                
                .btn-primary {
                    background: var(--primary);
                    color: white;
                }
                
                .btn-secondary {
                    background: var(--gray-light);
                    color: var(--gray);
                }
                
                .btn:hover {
                    transform: translateY(-1px);
                }
                
                .stats-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                    gap: 20px;
                    margin-bottom: 30px;
                }
                
                .stat-card {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 20px;
                    box-shadow: var(--shadow);
                    text-align: center;
                }
                
                .stat-icon {
                    width: 50px;
                    height: 50px;
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 0 auto 12px;
                    font-size: 20px;
                }
                
                .stat-value {
                    font-size: 24px;
                    font-weight: 700;
                    margin-bottom: 4px;
                }
                
                .stat-label {
                    color: var(--gray);
                    font-size: 14px;
                }
                
                .transactions-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: var(--border-radius);
                    padding: 24px;
                    box-shadow: var(--shadow);
                }
                
                .section-title {
                    font-size: 18px;
                    font-weight: 700;
                    margin-bottom: 20px;
                    color: var(--dark);
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                }
                
                .transaction-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 20px;
                }
                
                .transaction-table th {
                    background: var(--gray-light);
                    padding: 12px 16px;
                    text-align: left;
                    font-weight: 600;
                    color: var(--dark);
                    border-bottom: 2px solid var(--gray-light);
                }
                
                .transaction-table td {
                    padding: 12px 16px;
                    border-bottom: 1px solid var(--gray-light);
                    vertical-align: middle;
                }
                
                .transaction-row:hover {
                    background: rgba(103, 126, 234, 0.05);
                }
                
                .transaction-type {
                    display: inline-flex;
                    align-items: center;
                    gap: 6px;
                    padding: 4px 8px;
                    border-radius: 20px;
                    font-size: 12px;
                    font-weight: 600;
                }
                
                .transaction-type.income {
                    background: rgba(16, 185, 129, 0.1);
                    color: var(--success);
                }
                
                .transaction-type.expense {
                    background: rgba(239, 68, 68, 0.1);
                    color: var(--danger);
                }
                
                .transaction-amount {
                    font-weight: 600;
                    font-size: 16px;
                }
                
                .transaction-amount.income {
                    color: var(--success);
                }
                
                .transaction-amount.expense {
                    color: var(--danger);
                }
                
                .transaction-category {
                    background: var(--gray-light);
                    color: var(--gray);
                    padding: 2px 6px;
                    border-radius: 4px;
                    font-size: 12px;
                    font-weight: 500;
                }
                
                .empty-state {
                    text-align: center;
                    padding: 60px 20px;
                    color: var(--gray);
                }
                
                .empty-state i {
                    font-size: 64px;
                    margin-bottom: 20px;
                    opacity: 0.3;
                }
                
                @media (max-width: 768px) {
                    .filters-grid {
                        grid-template-columns: 1fr;
                    }
                    
                    .transaction-table {
                        font-size: 14px;
                    }
                    
                    .transaction-table th,
                    .transaction-table td {
                        padding: 8px 12px;
                    }
                    
                    .header {
                        flex-direction: column;
                        gap: 20px;
                        text-align: center;
                    }
                }
                """);
            
            html.append("</style></head><body>");
            
            html.append("<div class='container'>");
            
            // Header
            html.append("<div class='header'>");
            html.append("<div class='page-title'>");
            html.append("<i class='fas fa-list'></i>");
            html.append("Transaction Management");
            html.append("</div>");
            html.append("<a href='/' class='back-btn'>");
            html.append("<i class='fas fa-arrow-left'></i> Back to Dashboard");
            html.append("</a>");
            html.append("</div>");
            
            // Statistics Cards
            html.append("<div class='stats-grid'>");
            
            int totalTransactions = expenses.size() + incomes.size();
            BigDecimal totalExpenseAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalIncomeAmount = incomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Total Transactions
            html.append("<div class='stat-card'>");
            html.append("<div class='stat-icon' style='background: rgba(59, 130, 246, 0.1); color: var(--info);'>");
            html.append("<i class='fas fa-list'></i>");
            html.append("</div>");
            html.append("<div class='stat-value'>").append(totalTransactions).append("</div>");
            html.append("<div class='stat-label'>Total Transactions</div>");
            html.append("</div>");
            
            // Total Income
            html.append("<div class='stat-card'>");
            html.append("<div class='stat-icon' style='background: rgba(16, 185, 129, 0.1); color: var(--success);'>");
            html.append("<i class='fas fa-arrow-up'></i>");
            html.append("</div>");
            html.append("<div class='stat-value'>$").append(String.format("%.2f", totalIncomeAmount.doubleValue())).append("</div>");
            html.append("<div class='stat-label'>Total Income</div>");
            html.append("</div>");
            
            // Total Expenses
            html.append("<div class='stat-card'>");
            html.append("<div class='stat-icon' style='background: rgba(239, 68, 68, 0.1); color: var(--danger);'>");
            html.append("<i class='fas fa-arrow-down'></i>");
            html.append("</div>");
            html.append("<div class='stat-value'>$").append(String.format("%.2f", totalExpenseAmount.doubleValue())).append("</div>");
            html.append("<div class='stat-label'>Total Expenses</div>");
            html.append("</div>");
            
            // Net Balance
            BigDecimal netBalance = totalIncomeAmount.subtract(totalExpenseAmount);
            html.append("<div class='stat-card'>");
            html.append("<div class='stat-icon' style='background: rgba(245, 158, 11, 0.1); color: var(--warning);'>");
            html.append("<i class='fas fa-balance-scale'></i>");
            html.append("</div>");
            html.append("<div class='stat-value'>$").append(String.format("%.2f", netBalance.doubleValue())).append("</div>");
            html.append("<div class='stat-label'>Net Balance</div>");
            html.append("</div>");
            
            html.append("</div>");
            
            // Filters Section
            html.append("<div class='filters-section'>");
            html.append("<h3 class='filters-title'><i class='fas fa-filter'></i> Search & Filter</h3>");
            html.append("<form method='GET'>");
            html.append("<div class='filters-grid'>");
            
            html.append("<div class='filter-group'>");
            html.append("<label class='filter-label'>Search</label>");
            html.append("<input type='text' name='search' class='filter-input' placeholder='Search by description...' value='").append(searchTerm).append("'>");
            html.append("</div>");
            
            html.append("<div class='filter-group'>");
            html.append("<label class='filter-label'>Category</label>");
            html.append("<select name='category' class='filter-select'>");
            html.append("<option value=''>All Categories</option>");
            for (Category category : categories) {
                String selected = category.getId() == Integer.parseInt(categoryFilter.isEmpty() ? "0" : categoryFilter) ? " selected" : "";
                html.append("<option value='").append(category.getId()).append("'").append(selected).append(">");
                html.append(category.getName()).append("</option>");
            }
            html.append("</select>");
            html.append("</div>");
            
            html.append("<div class='filter-group'>");
            html.append("<label class='filter-label'>Type</label>");
            html.append("<select name='type' class='filter-select'>");
            html.append("<option value=''").append(typeFilter.isEmpty() ? " selected" : "").append(">All Types</option>");
            html.append("<option value='income'").append("income".equals(typeFilter) ? " selected" : "").append(">Income</option>");
            html.append("<option value='expense'").append("expense".equals(typeFilter) ? " selected" : "").append(">Expenses</option>");
            html.append("</select>");
            html.append("</div>");
            
            html.append("<div class='filter-group'>");
            html.append("<label class='filter-label'>From Date</label>");
            html.append("<input type='date' name='dateFrom' class='filter-input' value='").append(dateFrom).append("'>");
            html.append("</div>");
            
            html.append("<div class='filter-group'>");
            html.append("<label class='filter-label'>To Date</label>");
            html.append("<input type='date' name='dateTo' class='filter-input' value='").append(dateTo).append("'>");
            html.append("</div>");
            
            html.append("</div>");
            
            html.append("<div class='filter-actions'>");
            html.append("<button type='submit' class='btn btn-primary'>");
            html.append("<i class='fas fa-search'></i> Apply Filters");
            html.append("</button>");
            html.append("<a href='/transactions' class='btn btn-secondary'>");
            html.append("<i class='fas fa-times'></i> Clear Filters");
            html.append("</a>");
            html.append("</div>");
            
            html.append("</form>");
            html.append("</div>");
            
            // Transactions Table
            html.append("<div class='transactions-section'>");
            html.append("<div class='section-title'>");
            html.append("<span><i class='fas fa-table'></i> All Transactions</span>");
            html.append("<span>").append(totalTransactions).append(" Results</span>");
            html.append("</div>");
            
            if (totalTransactions == 0) {
                html.append("<div class='empty-state'>");
                html.append("<i class='fas fa-inbox'></i>");
                html.append("<h3>No Transactions Found</h3>");
                html.append("<p>No transactions match your current filters. Try adjusting your search criteria.</p>");
                html.append("</div>");
            } else {
                html.append("<table class='transaction-table'>");
                html.append("<thead>");
                html.append("<tr>");
                html.append("<th>Date</th>");
                html.append("<th>Type</th>");
                html.append("<th>Description</th>");
                html.append("<th>Category</th>");
                html.append("<th>Amount</th>");
                html.append("</tr>");
                html.append("</thead>");
                html.append("<tbody>");
                
                // Combine and sort transactions
                List<TransactionItem> allTransactions = new ArrayList<>();
                
                for (Expense expense : expenses) {
                    Category category = categories.stream()
                        .filter(c -> c.getId() == expense.getCategoryId())
                        .findFirst().orElse(null);
                    
                    allTransactions.add(new TransactionItem(
                        expense.getExpenseDate(),
                        "expense",
                        expense.getDescription(),
                        category != null ? category.getName() : "Unknown",
                        expense.getAmount()
                    ));
                }
                
                for (Income income : incomes) {
                    Category category = categories.stream()
                        .filter(c -> c.getId() == income.getCategoryId())
                        .findFirst().orElse(null);
                    
                    allTransactions.add(new TransactionItem(
                        income.getIncomeDate(),
                        "income",
                        income.getDescription(),
                        category != null ? category.getName() : "Unknown",
                        income.getAmount()
                    ));
                }
                
                // Sort by date (newest first)
                allTransactions.sort((a, b) -> b.date.compareTo(a.date));
                
                // Apply type filter
                if (!typeFilter.isEmpty()) {
                    allTransactions = allTransactions.stream()
                        .filter(t -> t.type.equals(typeFilter))
                        .collect(java.util.stream.Collectors.toList());
                }
                
                for (TransactionItem transaction : allTransactions) {
                    html.append("<tr class='transaction-row'>");
                    html.append("<td>").append(transaction.date.toString()).append("</td>");
                    
                    html.append("<td>");
                    html.append("<span class='transaction-type ").append(transaction.type).append("'>");
                    html.append("<i class='fas fa-").append(transaction.type.equals("income") ? "arrow-up" : "arrow-down").append("'></i>");
                    html.append(transaction.type.substring(0, 1).toUpperCase() + transaction.type.substring(1));
                    html.append("</span>");
                    html.append("</td>");
                    
                    html.append("<td>").append(transaction.description).append("</td>");
                    html.append("<td><span class='transaction-category'>").append(transaction.category).append("</span></td>");
                    
                    html.append("<td>");
                    html.append("<span class='transaction-amount ").append(transaction.type).append("'>");
                    html.append(transaction.type.equals("income") ? "+$" : "-$");
                    html.append(String.format("%.2f", transaction.amount.doubleValue()));
                    html.append("</span>");
                    html.append("</td>");
                    
                    html.append("</tr>");
                }
                
                html.append("</tbody>");
                html.append("</table>");
            }
            
            html.append("</div>");
            html.append("</div>");
            html.append("</body></html>");
            
            return html.toString();
        }
        
        private List<Expense> filterExpenses(List<Expense> expenses, String searchTerm, String categoryFilter, String dateFrom, String dateTo) {
            return expenses.stream()
                .filter(expense -> {
                    // Search filter
                    if (!searchTerm.isEmpty() && !expense.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) {
                        return false;
                    }
                    
                    // Category filter
                    if (!categoryFilter.isEmpty() && expense.getCategoryId() != Integer.parseInt(categoryFilter)) {
                        return false;
                    }
                    
                    // Date range filter
                    if (!dateFrom.isEmpty()) {
                        LocalDate fromDate = LocalDate.parse(dateFrom);
                        if (expense.getExpenseDate().isBefore(fromDate)) {
                            return false;
                        }
                    }
                    
                    if (!dateTo.isEmpty()) {
                        LocalDate toDate = LocalDate.parse(dateTo);
                        if (expense.getExpenseDate().isAfter(toDate)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
        }
        
        private List<Income> filterIncomes(List<Income> incomes, String searchTerm, String categoryFilter, String dateFrom, String dateTo) {
            return incomes.stream()
                .filter(income -> {
                    // Search filter
                    if (!searchTerm.isEmpty() && !income.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) {
                        return false;
                    }
                    
                    // Category filter
                    if (!categoryFilter.isEmpty() && income.getCategoryId() != Integer.parseInt(categoryFilter)) {
                        return false;
                    }
                    
                    // Date range filter
                    if (!dateFrom.isEmpty()) {
                        LocalDate fromDate = LocalDate.parse(dateFrom);
                        if (income.getIncomeDate().isBefore(fromDate)) {
                            return false;
                        }
                    }
                    
                    if (!dateTo.isEmpty()) {
                        LocalDate toDate = LocalDate.parse(dateTo);
                        if (income.getIncomeDate().isAfter(toDate)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
        }
        
        private Map<String, String> parseQueryParams(String query) {
            Map<String, String> params = new HashMap<>();
            if (query.isEmpty()) return params;
            
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        params.put(key, value);
                    } catch (Exception e) {
                        // Skip malformed parameters
                    }
                }
            }
            return params;
        }
        
        // Helper class for combined transaction display
        private static class TransactionItem {
            LocalDate date;
            String type;
            String description;
            String category;
            BigDecimal amount;
            
            TransactionItem(LocalDate date, String type, String description, String category, BigDecimal amount) {
                this.date = date;
                this.type = type;
                this.description = description;
                this.category = category;
                this.amount = amount;
            }
        }
    }

    static class MonthlyDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "{\"months\":[],\"income\":[],\"expenses\":[]}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        }
    }
    
    // Helper method to parse form data
    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> result = new HashMap<>();
        if (formData != null && !formData.isEmpty()) {
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        result.put(key, value);
                    } catch (Exception e) {
                        // Skip malformed data
                    }
                }
            }
        }
        return result;
    }
}
