package com.bookings.service.repository.booking;

import com.bookings.service.model.booking.Booking;
import com.bookings.service.model.user.User;
import com.bookings.service.repository.booking.mongodb.BookingMongoRepository;
import com.bookings.service.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    @Autowired
    private BookingMongoRepository bookingMongoRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Booking> getAllBookings() {
        return bookingMongoRepository.findAll();
    }

    @Override
    public Booking findBookingById(String idBooking) {
        Optional<Booking> optionalBooking = bookingMongoRepository.findById(idBooking);
        return optionalBooking.orElse(null);
    }
    @Override
    public Booking saveBooking(Booking booking) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // guarda un usuario nuevo desde booking
        List<User> savedUsers = new ArrayList<>();
        for (User user : booking.getUserData()) {
            if (!user.getPassword().startsWith("$2a$")) {
                // Si no está codificada, la codifica antes de guardar el usuario
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            User savedUser = userRepository.saveUser(user);
            savedUsers.add(savedUser);
        }

        // Set the saved users to the booking
        booking.setUserData(savedUsers);

        booking.setStartBooking(LocalDateTime.now());
        booking.setEndBooking(booking.getStartBooking().plusDays(booking.getDurationBooking()));

        // guardar booking
        return bookingMongoRepository.save(booking);
    }


    @Override
    public Boolean updateBooking(String idBooking, Booking updatedBooking) {
        Optional<Booking> searchBooking = bookingMongoRepository.findById(idBooking);

        if (searchBooking.isPresent()) {
            Booking existingBooking = searchBooking.get();

            // Update booking information
            existingBooking.setNameHotel(updatedBooking.getNameHotel());
            existingBooking.setRoomNumber(updatedBooking.getRoomNumber());
            existingBooking.setStartBooking(updatedBooking.getStartBooking());
            existingBooking.setEndBooking(updatedBooking.getEndBooking());

            // Update or add user information
            List<User> updatedUsers = updatedBooking.getUserData();
            List<User> existingUsers = existingBooking.getUserData();

            for (User updatedUser : updatedUsers) {
                Optional<User> existingUserOptional = existingUsers.stream()
                        .filter(user -> user.getId().equals(updatedUser.getId()))
                        .findFirst();

                if (existingUserOptional.isPresent()) {
                    // Update existing user details
                    User existingUser = existingUserOptional.get();
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setBirthDate(updatedUser.getBirthDate());
                    existingUser.setEmail(updatedUser.getEmail());
                    // Update other user details as needed
                } else {
                    // Add a new user if not found
                    existingUsers.add(updatedUser);
                }
            }

            // Save the updated booking (this should also cascade the changes to users)
            bookingMongoRepository.save(existingBooking);
            return true;
        }

        return false;
    }



    @Override
    public Boolean deleteBooking(String idBooking) {
        Optional<Booking> findBooking = bookingMongoRepository.findById(idBooking);
        if(findBooking.isPresent()){
            bookingMongoRepository.deleteById(idBooking);
            return true;
        }
        return false;
    }

}
