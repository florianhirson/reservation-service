package org.acme.reservation.rental;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Rental {

    private final Long id;
    private final String userId;
    private final Long reservationId;
    private final LocalDate startDate;
}
