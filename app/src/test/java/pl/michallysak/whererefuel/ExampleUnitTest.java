package pl.michallysak.whererefuel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    private float getDistance(double lat, double lng, double latNow, double lngNow) {
        return (float)Math.sqrt(Math.pow((lat - latNow) * 110.574, 2) + (Math.pow((lng - lngNow) * 111.320 * Math.cos(Math.toRadians(lat)), 2)) );
    }

    @Test
    public void dis(){
        float distance = getDistance(5, 2, 5, 2);
        assertEquals(0f, distance, distance);
    }

}