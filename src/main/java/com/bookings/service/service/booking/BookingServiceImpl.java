package com.bookings.service.service.booking;

import com.bookings.service.dto.booking.BookingDto;
import com.bookings.service.dto.booking.BookingMapper;
import com.bookings.service.dto.booking.BookingResponseDto;
import com.bookings.service.dto.user.UserDto;
import com.bookings.service.dto.user.UserMapper;
import com.bookings.service.dto.user.UserResponseDto;
import com.bookings.service.model.booking.Booking;
import com.bookings.service.repository.booking.BookingRepository;
import com.bookings.service.repository.user.UserRepository;
import com.bookings.service.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<BookingResponseDto> getAllBooking() {
        List<BookingResponseDto> responseDtoList = new ArrayList<>();
        bookingRepository.getAllBookings().forEach(booking -> responseDtoList.add(BookingMapper.bookingToBookingResponseDto(booking)));
        return responseDtoList;
    }

    @Override
    public Optional<BookingResponseDto> findBookingById(String idBooking) {
        Booking booking = bookingRepository.findBookingById(idBooking);
        if(booking != null){
            return Optional.of(BookingMapper.bookingToBookingResponseDto(booking));
        }else {
            return Optional.empty();
        }
    }

    @Override
    public BookingResponseDto saveBooking(BookingDto bookingDto) {
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.saveBooking(BookingMapper.bookingDtoToBooking(bookingDto)));
    }

    @Override
    public BookingResponseDto saveBookingWithUser(BookingDto bookingDto) {
        // Save the user along with the booking
        UserDto userDto = new UserDto(
                bookingDto.getUserData().get(0).getFirstName(),
                bookingDto.getUserData().get(0).getLastName(),
                bookingDto.getUserData().get(0).getEmail(),
                bookingDto.getUserData().get(0).getPassword()
        );

        UserResponseDto savedUser = userService.saveUser(userDto);

        // Create a new BookingDto with the saved user
        BookingDto newBookingDto = new BookingDto(
                bookingDto.getNameHotel(),
                bookingDto.getRoomNumber(),
                bookingDto.getStartBooking(),
                bookingDto.getDurationBooking(),
                bookingDto.getEndBooking(),
                Collections.singletonList(UserMapper.userResponseDtoToUser(savedUser))
                );

        // Save the booking with the updated BookingDto
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.saveBooking(BookingMapper.bookingDtoToBooking(newBookingDto)));
    }


    @Override
    public Boolean updateBooking(String idBooking, BookingDto bookingDto) {
        return bookingRepository.updateBooking(idBooking, BookingMapper.bookingDtoToBooking(bookingDto));
    }

    @Override
    public Boolean deleteBooking(String idBooking) {
        return bookingRepository.deleteBooking(idBooking);
    }
}
