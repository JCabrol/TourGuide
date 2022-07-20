package tourGuide.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

@Setter
@Getter
@NoArgsConstructor
public class UserPreferences {

    /**
     * The user's preferred distance for attractions. Default value is 2^31 - 1.
     */
    private int attractionProximity = Integer.MAX_VALUE;

    /**
     * The currency used by the user. Default value is "USD" (american dollar).
     */
    private CurrencyUnit currency = Monetary.getCurrency("USD");
    /**
     * The user's minimal price for trips. Default value is 0.
     */
    private Money lowerPricePoint = Money.of(0, currency);
    /**
     * The user's maximal price for trips. Default value is 2^31 - 1.
     */
    private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);

    /**
     * The user's chosen number of days for a trip. Default value is 5.
     */
    private int tripDuration = 5;

    /**
     * The user's chosen number of tickets for attractions. Default value is 1.
     */
    private int ticketQuantity = 1;

    /**
     * The user's chosen number of adults for a trip. Default value is 2.
     */
    private int numberOfAdults = 2;

    /**
     * The user's chosen number of children for a trip. Default value is 2.
     */
    private int numberOfChildren = 2;
}
