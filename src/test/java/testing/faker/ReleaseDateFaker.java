package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.ReleaseDate;
import com.odeyalo.sonata.playlists.model.ReleaseDate.Precision;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReleaseDateFaker {
    private static final int MIN_YEAR = 1500;
    private static final int MAX_YEAR = 2024;

    private final ReleaseDate.ReleaseDateBuilder builder = ReleaseDate.builder();

    private final Faker faker = Faker.instance();

    public ReleaseDateFaker() {
        Precision releaseDatePrecision = faker.options().option(Precision.class);

        builder.precision(releaseDatePrecision);

        if ( releaseDatePrecision == Precision.DAY ) {
            randomForDayPrecision();
        }

        if ( releaseDatePrecision == Precision.MONTH ) {
            randomForMonthPrecision();
        }

        if ( releaseDatePrecision == Precision.YEAR ) {
            randomForYearPrecision();
        }
    }

    public static ReleaseDateFaker randomReleaseDate() {
        return new ReleaseDateFaker();
    }

    public ReleaseDateFaker precision(@NotNull Precision precision) {
        builder.precision(precision);
        return this;
    }

    public ReleaseDateFaker year(@NotNull Integer year) {
        builder.year(year);
        return this;
    }

    public ReleaseDateFaker month(@Nullable Integer month) {
        builder.month(month);
        return this;
    }

    public ReleaseDateFaker day(@Nullable Integer day) {
        builder.day(day);
        return this;
    }

    public ReleaseDate get() {
        return builder.build();
    }

    private ReleaseDate.ReleaseDateBuilder randomForYearPrecision() {
        Integer year = faker.random().nextInt(MIN_YEAR, MAX_YEAR);
        return builder.year(year);
    }

    private ReleaseDate.ReleaseDateBuilder randomForMonthPrecision() {
        Integer monthNumber = faker.random().nextInt(1, 12);
        return randomForYearPrecision()
                .month(monthNumber);
    }

    private ReleaseDate.ReleaseDateBuilder randomForDayPrecision() {
        return randomForMonthPrecision()
                .day(faker.random().nextInt(1, 31));
    }
}
