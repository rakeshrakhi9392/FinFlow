package com.reimbursement.enums;

import java.time.LocalDate;
import java.time.Month;

public enum FiscalQuarter {
    Q1,
    Q2,
    Q3,
    Q4;

    public static FiscalQuarter fromDate(LocalDate date) {
        Month month = date.getMonth();
        return switch (month) {
            case JANUARY, FEBRUARY, MARCH -> Q1;
            case APRIL, MAY, JUNE -> Q2;
            case JULY, AUGUST, SEPTEMBER -> Q3;
            case OCTOBER, NOVEMBER, DECEMBER -> Q4;
        };
    }

    public static int fiscalYearOf(LocalDate date) {
        return date.getYear();
    }
}
