package org.acme.reservation.rest;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.rental.Rental;
import org.acme.reservation.rental.RentalClient;
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.reservation.ReservationsRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ReservationResource {

    private final ReservationsRepository reservationsRepository;

    @RestClient
    private final RentalClient rentalClient;

    @GraphQLClient("inventory")
    private final GraphQLInventoryClient graphQLInventoryClient;

    @GET
    @Path("availability")
    public Collection<Car> availability(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {
        // obtain all cars from inventory
        List<Car> availableCars = graphQLInventoryClient.allCars();

        // create a map from did to car
        Map<Long, Car> carsById = new HashMap<>();
        for (Car car : availableCars) {
            carsById.put(car.getId(), car);
        }

        // get all current reservations
        List<Reservation> reservations = reservationsRepository.findAll();
        // for each reservation, remove the car from the map
        for (Reservation reservation : reservations) {
            if(reservation.isReserved(startDate, endDate)) {
                carsById.remove(reservation.getCarId());
            }
        }

        return carsById.values();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Reservation make(Reservation reservation) {
        Reservation result = reservationsRepository.save(reservation);

        // dummy value for the time being
        String userId = "x";
        if(reservation.getStartDay().equals(LocalDate.now())) {
            Rental rental = rentalClient.start(userId, result.getId());
            Log.info("Rental started: " + rental);
        }

        return result;
    }
}
