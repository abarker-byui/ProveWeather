package byui.cs246.barker.wk06Prove;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.lang.ref.WeakReference;

import byui.cs246.barker.wk06Prove.objects.GetWeatherType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onGetTemperatureClick(View view) {
        Runnable tempRunnable = new GetWeather(getCity(), new WeakReference<Activity>(this), GetWeatherType.Current);
        Thread thread = new Thread(tempRunnable);
        thread.start();
    }

    public void onGetForecastClick(View view) {
        Runnable tempRunnable = new GetWeather(getCity(), new WeakReference<Activity>(this), GetWeatherType.Forecast);
        Thread thread = new Thread(tempRunnable);
        thread.start();
    }

    private String getCity() {
        EditText txtCity = (EditText)findViewById(R.id.editCity);
        return txtCity.getText().toString();
    }
}
