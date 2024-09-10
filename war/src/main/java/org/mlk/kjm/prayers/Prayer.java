package org.mlk.kjm.prayers;

import java.time.LocalDate;

import org.mlk.kjm.inmates.Inmate;

public class Prayer {
    private final Inmate inmate;
    private final LocalDate date;
    private final String prayer;

    public Prayer(Inmate inmate, LocalDate date, String prayer) {
        this.inmate = inmate;
        this.date = date;
        this.prayer = prayer;
    }

    public Inmate getInmate() {
        return inmate;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPrayer() {
        return prayer;
    }
}
