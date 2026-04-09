package com.tendanz.pricing;

import com.tendanz.pricing.dto.QuoteRequest;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.entity.PricingRule;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.entity.Quote;
import com.tendanz.pricing.entity.Zone;
import com.tendanz.pricing.repository.PricingRuleRepository;
import com.tendanz.pricing.repository.ProductRepository;
import com.tendanz.pricing.repository.QuoteRepository;
import com.tendanz.pricing.repository.ZoneRepository;
import com.tendanz.pricing.service.PricingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PricingService.
 *
 * TODO: Implement at least 5 test cases covering:
 * - Quote calculation for different age categories (YOUNG, ADULT, SENIOR, ELDERLY)
 * - Different zone risk coefficients
 * - Edge cases (minimum age 18, maximum age 99, boundary between categories)
 * - Error handling (invalid product ID, invalid zone code)
 * - Quote retrieval by ID
 *
 * The @BeforeEach setUp() method below creates test data you can use.
 * Add your test methods below the existing structure.
 */
@DataJpaTest
@Import({PricingService.class, ObjectMapper.class})
class PricingServiceTest {

    @Autowired
    private PricingService pricingService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private PricingRuleRepository pricingRuleRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    private Product product;
    private Zone zone;
    private PricingRule pricingRule;

    @BeforeEach
    void setUp() {
        // Test data: Auto Insurance, zone coefficient 1.20, standard age factors
        product = Product.builder()
                .name("Test Auto Insurance")
                .description("Test Description")
                .createdAt(LocalDateTime.now())
                .build();
        productRepository.save(product);

        zone = Zone.builder()
                .code("BENAROUS")
                .name("Grand Tunis")
                .riskCoefficient(BigDecimal.valueOf(1.20))
                .build();
        zoneRepository.save(zone);

        pricingRule = PricingRule.builder()
                .product(product)
                .baseRate(BigDecimal.valueOf(500.00))
                .ageFactorYoung(BigDecimal.valueOf(1.30))
                .ageFactorAdult(BigDecimal.valueOf(1.00))
                .ageFactorSenior(BigDecimal.valueOf(1.20))
                .ageFactorElderly(BigDecimal.valueOf(1.50))
                .createdAt(LocalDateTime.now())
                .build();
        pricingRuleRepository.save(pricingRule);
    }

    /**
     * TODO: Test quote calculation for an adult client (age 25-45).
     *
     * Expected: 500.00 × 1.00 (adult) × 1.20 (Tunis) = 600.00 TND
     */
    @Test
    void testCalculateQuoteForAdult() {
        // Hint: Use QuoteRequest.builder() to create the request
        // Then call pricingService.calculateQuote(request)
        // Assert: finalPrice == 600.00, basePrice == 500.00, etc.
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode("BENAROUS")
                .clientAge(30)
                .clientName("Jihene Saad")
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(response.getFinalPrice()).isEqualByComparingTo(BigDecimal.valueOf(600.00));
        assertThat(response.getClientAge()).isEqualTo(30);
        assertThat(response.getClientName()).isEqualTo("Jihene Saad");
    }

    /**
     * TODO: Test quote calculation for a young client (age 18-24).
     *
     * Expected: 500.00 × 1.30 (young) × 1.20 (Tunis) = 780.00 TND
     */
    @Test
    void testCalculateQuoteForYoungClient() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode("BENAROUS")
                .clientAge(20)
                .clientName("Jihene Saad")
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(response.getFinalPrice()).isEqualByComparingTo(BigDecimal.valueOf(780.00));
        assertThat(response.getClientAge()).isEqualTo(20);
        assertThat(response.getClientName()).isEqualTo("Jihene Saad");
    }

    /**
     * TODO: Test quote calculation for a senior client (age 46-65).
     *
     * Expected: 500.00 × 1.20 (senior) × 1.20 (Tunis) = 720.00 TND
     */
    @Test
    void testCalculateQuoteForSeniorClient() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode("BENAROUS")
                .clientAge(50)
                .clientName("Jihene Saad")
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(response.getFinalPrice()).isEqualByComparingTo(BigDecimal.valueOf(720.00));
        assertThat(response.getClientAge()).isEqualTo(50);
        assertThat(response.getClientName()).isEqualTo("Jihene Saad");
    }

    /**
     * TODO: Test that requesting a quote with an invalid product ID
     * throws IllegalArgumentException.
     */
    @Test
    void testCalculateQuoteWithInvalidProductId() {
        // Hint: Use assertThrows(IllegalArgumentException.class, () -> ...)
        QuoteRequest request = QuoteRequest.builder()
                .productId(999L)
                .zoneCode("BENAROUS")
                .clientAge(30)
                .clientName("Jihene Saad")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> pricingService.calculateQuote(request));
    }

    /**
     * TODO: Test that requesting a quote with an invalid zone code
     * throws IllegalArgumentException.
     */
    @Test
    void testCalculateQuoteWithInvalidZoneCode() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode("INVALID")
                .clientAge(30)
                .clientName("Jihene Saad")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> pricingService.calculateQuote(request));
    }

    /**
     * TODO: (Bonus) Test quote retrieval by ID.
     * Create a quote, then retrieve it with pricingService.getQuote(id).
     * Verify all fields match.
     */
    @Test
    void testCreateAndRetrieveQuoteById() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode(zone.getCode())
                .clientAge(30)
                .clientName("Jihene Saad")
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);
        QuoteResponse retrieved = pricingService.getQuote(response.getQuoteId());

        assertThat(retrieved.getQuoteId()).isEqualTo(response.getQuoteId());
        assertThat(retrieved.getClientName()).isEqualTo("Jihene Saad");
        assertThat(retrieved.getClientAge()).isEqualTo(30);
        assertThat(retrieved.getBasePrice()).isEqualByComparingTo(response.getBasePrice());
        assertThat(retrieved.getFinalPrice()).isEqualByComparingTo(response.getFinalPrice());
        assertThat(retrieved.getZoneName()).isEqualTo(zone.getName()); // ou zone.getName()
        assertThat(retrieved.getProductName()).isEqualTo(product.getName());
    }

    /**
     * TODO: (Bonus) Test edge cases: age boundaries.
     * - Age 24 should be YOUNG, age 25 should be ADULT
     * - Age 45 should be ADULT, age 46 should be SENIOR
     * - Age 65 should be SENIOR, age 66 should be ELDERLY
     */
    @Test
    void testAgeBoundaries() {
        // Arrange + Act
        QuoteResponse young24 = pricingService.calculateQuote(
                QuoteRequest.builder().productId(product.getId()).zoneCode(zone.getCode()).clientAge(24).clientName("Mariam").build()
        );
        QuoteResponse adult25 = pricingService.calculateQuote(
                QuoteRequest.builder().productId(product.getId()).zoneCode(zone.getCode()).clientAge(25).clientName("Ahmed").build()
        );

        QuoteResponse adult45 = pricingService.calculateQuote(
                QuoteRequest.builder().productId(product.getId()).zoneCode(zone.getCode()).clientAge(45).clientName("Aicha").build()
        );
        QuoteResponse senior46 = pricingService.calculateQuote(
                QuoteRequest.builder().productId(product.getId()).zoneCode(zone.getCode()).clientAge(46).clientName("Mohamed").build()
        );

        QuoteResponse senior65 = pricingService.calculateQuote(
                QuoteRequest.builder().productId(product.getId()).zoneCode(zone.getCode()).clientAge(65).clientName("Fatma").build()
        );
        QuoteResponse elderly66 = pricingService.calculateQuote(
                QuoteRequest.builder().productId(product.getId()).zoneCode(zone.getCode()).clientAge(66).clientName("Ali").build()
        );

        //Assert
        assertThat(young24.getFinalPrice()).isEqualByComparingTo(
                pricingRule.getBaseRate().multiply(pricingRule.getAgeFactorYoung()).multiply(zone.getRiskCoefficient())
        );
        assertThat(adult25.getFinalPrice()).isEqualByComparingTo(
                pricingRule.getBaseRate().multiply(pricingRule.getAgeFactorAdult()).multiply(zone.getRiskCoefficient())
        );

        assertThat(adult45.getFinalPrice()).isEqualByComparingTo(
                pricingRule.getBaseRate().multiply(pricingRule.getAgeFactorAdult()).multiply(zone.getRiskCoefficient())
        );
        assertThat(senior46.getFinalPrice()).isEqualByComparingTo(
                pricingRule.getBaseRate().multiply(pricingRule.getAgeFactorSenior()).multiply(zone.getRiskCoefficient())
        );

        assertThat(senior65.getFinalPrice()).isEqualByComparingTo(
                pricingRule.getBaseRate().multiply(pricingRule.getAgeFactorSenior()).multiply(zone.getRiskCoefficient())
        );
        assertThat(elderly66.getFinalPrice()).isEqualByComparingTo(
                pricingRule.getBaseRate().multiply(pricingRule.getAgeFactorElderly()).multiply(zone.getRiskCoefficient())
        );
    }
}
