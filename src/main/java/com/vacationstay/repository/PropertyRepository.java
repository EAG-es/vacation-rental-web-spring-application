package com.vacationstay.repository;

import com.vacationstay.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByLocationContainingIgnoreCase(String location);
    
    List<Property> findByBedroomsGreaterThanEqual(Integer bedrooms);
    
    List<Property> findByBathroomsGreaterThanEqual(Integer bathrooms);
    
    List<Property> findByMaxGuestsGreaterThanEqual(Integer guests);
    
    List<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("SELECT p FROM Property p WHERE " +
           "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:bedrooms IS NULL OR p.bedrooms >= :bedrooms) AND " +
           "(:bathrooms IS NULL OR p.bathrooms >= :bathrooms) AND " +
           "(:guests IS NULL OR p.maxGuests >= :guests)")
    List<Property> findByFilters(
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("bedrooms") Integer bedrooms,
            @Param("bathrooms") Integer bathrooms,
            @Param("guests") Integer guests);
    
    List<Property> findByOwnerId(String ownerId);
}