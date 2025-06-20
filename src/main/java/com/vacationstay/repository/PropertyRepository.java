package com.vacationstay.repository;

import com.vacationstay.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Property entities.
 * <p>
 * This interface provides methods for database operations related to properties.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    /**
     * Finds properties by location (case-insensitive partial match).
     *
     * @param location the location to search for
     * @return a list of properties matching the location
     */
    List<Property> findByLocationContainingIgnoreCase(String location);
    
    /**
     * Finds properties with at least the specified number of bedrooms.
     *
     * @param bedrooms the minimum number of bedrooms
     * @return a list of properties with at least the specified number of bedrooms
     */
    List<Property> findByBedroomsGreaterThanEqual(Integer bedrooms);
    
    /**
     * Finds properties with at least the specified number of bathrooms.
     *
     * @param bathrooms the minimum number of bathrooms
     * @return a list of properties with at least the specified number of bathrooms
     */
    List<Property> findByBathroomsGreaterThanEqual(Integer bathrooms);
    
    /**
     * Finds properties that can accommodate at least the specified number of guests.
     *
     * @param guests the minimum number of guests
     * @return a list of properties that can accommodate at least the specified number of guests
     */
    List<Property> findByMaxGuestsGreaterThanEqual(Integer guests);
    
    /**
     * Finds properties within the specified price range.
     *
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return a list of properties within the specified price range
     */
    List<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Finds properties based on multiple filter criteria.
     * <p>
     * This query allows for flexible searching with optional parameters.
     * Any parameter can be null, in which case that filter is not applied.
     * </p>
     *
     * @param location the location to search for (optional)
     * @param minPrice the minimum price (optional)
     * @param maxPrice the maximum price (optional)
     * @param bedrooms the minimum number of bedrooms (optional)
     * @param bathrooms the minimum number of bathrooms (optional)
     * @param guests the minimum number of guests (optional)
     * @return a list of properties matching the applied filters
     */
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
    
    /**
     * Finds properties owned by a specific user.
     *
     * @param ownerId the ID of the owner
     * @return a list of properties owned by the specified user
     */
    List<Property> findByOwnerId(String ownerId);
}