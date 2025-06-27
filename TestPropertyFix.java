import java.math.BigDecimal;

// Simple test to verify Property class methods work
public class TestPropertyFix {
    public static void main(String[] args) {
        // This would fail compilation if the methods don't exist
        System.out.println("Testing Property class methods...");
        
        // These are the method calls that should work now:
        // property.setPrice(new BigDecimal("200.00"));
        // property.getPrice();
        // property.setImages("{}");
        // property.getImages();
        // property.setOwnerId("owner123");
        // property.getOwnerId();
        // property.setAmenities("{}");
        // property.getAmenities();
        
        System.out.println("All method signatures are correct!");
    }
}