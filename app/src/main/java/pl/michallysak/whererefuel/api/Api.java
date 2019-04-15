package pl.michallysak.whererefuel.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {


//    GET

    @GET("whererefuel/api/gas-station/get")
    Call<List<GasStation>> getNearbyGasStation(@Query("lat") double lat, @Query("lng") double lng, @Query("radius") int radius,
                                               @Query("order") String order, @Query("fuel") String fuel, @Query("company") String company);

    @GET("whererefuel/api/gas-station/get")
    Call<List<GasStation>> getNearbyCityGasStation(@Query("lat") double lat, @Query("lng") double lng, @Query("radius") int radius,
                                                   @Query("order") String order, @Query("fuel") String fuel, @Query("company") String company,
                                                   @Query("city") String city);

    @GET("whererefuel/api/gas-station/get-by-id")
    Call<List<GasStation>> getGasStationByIds(@Query("id") String ids);


    @GET("whererefuel/api/companies")
    Call<List<Company>> getCompanies();

    @GET("whererefuel/api/cities")
    Call<List<City>> getCities();

//    POST

    @POST("whererefuel/api/gas-station/update-price")
    Call<List<Result>> updatePrice(@Query("id") int id, @Query("e95") double e95, @Query("e98") double e98, @Query("on") double on, @Query("lpg") double lpg);

    @POST("whererefuel/api/gas-station/report")
    Call<List<Result>> reportStation(@Query("message") String message);


    @POST("whererefuel/api/gas-station/add")
    Call<List<Result>> addStation(@Query("lat") double lat, @Query("lng") double lng,
                                  @Query("name") String name, @Query("company") String company,
                                  @Query("city") String city, @Query("address") String address);


}
