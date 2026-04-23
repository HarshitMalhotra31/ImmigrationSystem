package com.immigration.service;

import com.immigration.model.EntryExit;
import com.immigration.model.Visa;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class OverstayService {
    public long calculateOverstayDays(EntryExit entryExit, Visa visa) {
        LocalDateTime entryDate = LocalDateTime.parse(entryExit.getEntryDate());
        LocalDateTime now = LocalDateTime.now();

        long daysElapsed = ChronoUnit.DAYS.between(entryDate, now);
        long visaDays = visa.getVisaDays();

        if (daysElapsed > visaDays) {
            return daysElapsed - visaDays;
        }
        return 0;
    }

    public double calculateFine(long overstayDays) {
        return overstayDays * 50.0; // $50 per day
    }
}
