package org.affordablehousing.chi.housingapp.data;

import android.util.Log;

import org.affordablehousing.chi.housingapp.model.Property;
import org.affordablehousing.chi.housingapp.service.GetPropertyDataService;
import org.affordablehousing.chi.housingapp.service.RetrofitClientInstance;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class PropertyRepository {

    private static int FRESH_TIMEOUT_IN_MINUTES = 60;

    private final GetPropertyDataService getPropertyDataService;
    private final PropertyDAO propertyDAO;
    private final Executor executor;
    private final String TAG = PropertyRepository.class.getSimpleName();
    private List<Property> propertyList;

    @Inject
    public PropertyRepository(GetPropertyDataService getPropertyDataService, PropertyDAO propertyDAO, Executor executor) {
       this.getPropertyDataService = RetrofitClientInstance.getRetrofitInstance().create(GetPropertyDataService.class);
        this.propertyDAO = propertyDAO;
        this.executor = executor;
    }

    // ---

    public LiveData<List<Property>> getProperties() {
        refreshProperties(); // try to refresh data if possible from Github Api
        return (LiveData <List <Property>>) propertyDAO.loadAllProperties();
}

    // ---

    private void refreshProperties() {
        executor.execute(() -> {
            // Check if user was fetched recently
            boolean propertiesNotLoaded = propertyDAO.loadAllProperties().isEmpty();
            // If user have to be updated
            if ( propertiesNotLoaded ) {
                //GetPropertyDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetPropertyDataService.class);
                getPropertyDataService.getAllProperties().enqueue(new Callback<List<Property>>() {
                    @Override
                    public void onResponse(Call<List<Property>> call, Response<List<Property>> response) {
                       // Toast.makeText(Application., "Data refreshed from network !", Toast.LENGTH_LONG).show();
                        executor.execute(() -> {
                            propertyList = response.body();
                            for (Property property : propertyList) {
                                propertyDAO.save(property);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Property>> call, Throwable t) {
                        Log.d(TAG , t.getMessage());
                    }
                });
            }
        });
    }


    private Date getMaxRefreshTime(Date currentDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MINUTE, -FRESH_TIMEOUT_IN_MINUTES);
        return cal.getTime();
    }
}
