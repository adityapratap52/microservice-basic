package com.hotel.controllers;

import com.hotel.entities.Hotel;
import com.hotel.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // create
    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.hotelService.create(hotel));
    }

    // get single data by id
    @GetMapping("/{hotelId}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable String hotelId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.hotelService.get(hotelId));
    }

    // get all data
    @GetMapping
    public ResponseEntity<List<Hotel>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.hotelService.getAll());
    }
}
