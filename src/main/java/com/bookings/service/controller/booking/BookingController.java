package com.bookings.service.controller.booking;

import com.bookings.service.dto.booking.BookingDto;
import com.bookings.service.dto.booking.BookingResponseDto;
import com.bookings.service.service.booking.BookingService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.bookings.service.utils.Constants.ADMIN_ROLE;


@RestController
@RequestMapping("/v1/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings(){
        try{
            return ResponseEntity.ok(bookingService.getAllBooking());
        }catch (Exception e){
            return new ResponseEntity("Error in getAllBookings controller: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{idBooking}")
    public ResponseEntity<?> findBookingById(@PathVariable String idBooking) {
        try {
            Optional<BookingResponseDto> bookingOptional = bookingService.findBookingById(idBooking);
            return bookingOptional
                    .map(b -> new ResponseEntity<>(b, HttpStatus.OK))
                    .orElse(new ResponseEntity("Booking not found with ID: " + idBooking, HttpStatus.NOT_FOUND));
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Booking not found with ID: " + idBooking, HttpStatus.NOT_FOUND);
        }
    }
    @RolesAllowed(ADMIN_ROLE)
    @PostMapping
    public ResponseEntity<?> saveBooking(@RequestBody BookingDto bookingDto){
        BookingResponseDto responseDto = bookingService.saveBooking(bookingDto);
        return new ResponseEntity<>(responseDto,HttpStatus.CREATED);
    }
    @RolesAllowed(ADMIN_ROLE)
    @PutMapping("/{idBooking}")
    public ResponseEntity<?> updateBooking(@PathVariable String idBooking, @RequestBody BookingDto bookingDto){
        try {
            Boolean isUpdateBooking = bookingService.updateBooking(idBooking, bookingDto);

            if (isUpdateBooking) {
                // Puedes devolver el objeto actualizado si es necesario
                Optional<BookingResponseDto> updatedBookingResponse = bookingService.findBookingById(idBooking);
                return new ResponseEntity<>(updatedBookingResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("The booking is not found", HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("The booking " + idBooking + " doesn't exist in the database.", HttpStatus.BAD_REQUEST);
        }
    }

    @RolesAllowed(ADMIN_ROLE)
    @DeleteMapping("/{idBooking}")
    public ResponseEntity<Boolean> deleteBooking(@PathVariable String idBooking){
        return new ResponseEntity(bookingService.deleteBooking(idBooking),HttpStatus.OK);
    }

}
