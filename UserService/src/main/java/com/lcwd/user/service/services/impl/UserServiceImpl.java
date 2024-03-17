package com.lcwd.user.service.services.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.external.services.RatingService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RestTemplate restTemplate;

    private static Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return this.userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on the server !! : " + userId));
                        // first way by using Rest Template
//        Rating[] ratingsOfUser = this.restTemplate.getForObject("http://RATING-SERVICE/ratings/users/" + user.getUserId(), Rating[].class);
//        List<Rating> ratings = ratingsOfUser != null ? Arrays.stream(ratingsOfUser).collect(Collectors.toList())
//                                : new ArrayList<>();

                        // second way by using Feign Client
        List<Rating> ratings = this.ratingService.getRatingByUserId(user.getUserId());

        LOG.info("Ratings of this particular user {} ",ratings);
            // peek is same working as a map
        List<Rating> ratingList = ratings.parallelStream().peek(rating -> {
                            // first way by using RestTemplate
//            ResponseEntity<Hotel> hotelResponse = this.restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/" + rating.getHotelId(), Hotel.class);
//            Hotel hotel = hotelResponse.getBody();

                            // second way by using Feign client
            Hotel hotel = this.hotelService.getHotel(rating.getHotelId());
            rating.setHotel(hotel);
        }).collect(Collectors.toList());
        user.setRatings(ratingList);
        return user;
    }
}
