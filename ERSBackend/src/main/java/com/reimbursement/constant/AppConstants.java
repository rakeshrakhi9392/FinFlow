package com.reimbursement.constant;

public final class AppConstants {

    private AppConstants() {
    }

    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USERNAME = "username";
    public static final String SESSION_ROLE = "role";

    public static final String DEFAULT_ROLE = "employee";
    public static final String DEFAULT_FIRST_NAME = "Unknown";
    public static final String DEFAULT_LAST_NAME = "User";

    public static final String CORS_ORIGIN_LOCAL = "http://localhost:3000";

    public static final String API_USERS = "/users";
    public static final String API_REIMBURSEMENTS = "/api/reimbursements";
    public static final String API_BUDGETS = "/api/budgets";
    public static final String API_CATEGORIES = "/api/categories";
    public static final String API_DEPARTMENTS = "/api/departments";
    public static final String API_WORKFLOW = "/api/workflow";
    public static final String API_VENDOR = "/api/vendor";
}
