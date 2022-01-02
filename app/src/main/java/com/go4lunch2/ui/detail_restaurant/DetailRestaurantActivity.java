package com.go4lunch2.ui.detail_restaurant;

import static com.go4lunch2.MyApplication.PREFS_NOTIFS;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch2.BaseActivity;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.databinding.ActivityDetailRestaurantBinding;
import com.go4lunch2.service.NotificationHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailRestaurantActivity extends BaseActivity {

    static public final String RESTAURANT_SELECTED = "restaurant_selected";
    private final List<CustomUser> workmatesInterested = new ArrayList<>();
    private DetailRestaurantViewModel vm;
    private CustomUser currentCustomUser;
    private DetailRestaurantViewState restaurantSelected;
    private ActivityDetailRestaurantBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationHelper.createNotificationChannel(this, getString(R.string.title_chanel), getString(R.string.desc_chanel));

        // Get the id of the restaurant
        Intent intent = getIntent();
        String idRestaurant = intent.getStringExtra(RESTAURANT_SELECTED);

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(DetailRestaurantViewModel.class);

        binding = ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // check if this restaurant has been selected by user, and change the icon color accordingly
        vm.getCurrentCustomUser(BaseActivity.user.getUid()).observe(this, value -> {
            currentCustomUser = value;
            if (currentCustomUser.getIdRestaurantChosen() != null && currentCustomUser.getIdRestaurantChosen().equals(idRestaurant)) {
                binding.fabRestaurantChosen.setColorFilter(this.getResources().getColor(R.color.green_select_fab));
            }
        });

        // button to go back at the top of the screen
        binding.backToMain.setOnClickListener(v -> onBackPressed());

        vm.getDetailRestaurantLiveData(idRestaurant).observe(this, restaurant -> {
            restaurantSelected = restaurant;

            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (restaurant == null) finish();
            else {
                binding.restaurantName.setText(restaurant.getName());
                binding.restaurantDesc1.setText(restaurant.getAdress());

                // If user selects this restaurant
                // Set the notification that will be sent at noon
                binding.fabRestaurantChosen.setOnClickListener(v -> {
                    vm.updateRestaurantChosen(currentUser.getUid(), idRestaurant, restaurant.getName());
                    if (MyApplication.settings.getBoolean(PREFS_NOTIFS, true)) {
                        Intent intentNotif = new Intent(this, ReminderBroadcast.class);
                        intentNotif.putExtra("nameRestaurant", restaurant.getName());
                        intentNotif.putExtra("idRestaurant", restaurant.getId());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intentNotif, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Calendar currentCal = Calendar.getInstance();
                        Calendar firingCal = Calendar.getInstance();
                        firingCal.set(Calendar.HOUR_OF_DAY, 12);
                        firingCal.set(Calendar.MINUTE, 0);
                        firingCal.set(Calendar.SECOND, 0);
                        if (firingCal.getTimeInMillis() < currentCal.getTimeInMillis()) firingCal.add(Calendar.DAY_OF_MONTH, 1);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, firingCal.getTimeInMillis(), pendingIntent);
                    }
                });

                if (restaurant.getImage() != null)
                    Glide.with(this).load(Uri.parse(restaurant.getImage())).into(binding.restaurantImage);

                // Display the stars according to the rate of this restaurant
                if (restaurant.getStarsCount() == null) {
                    binding.ivDetailStar1.setVisibility(View.GONE);
                    binding.ivDetailStar2.setVisibility(View.GONE);
                    binding.ivDetailStar3.setVisibility(View.GONE);
                }
                else {
                    if (restaurant.getStarsCount() == 0.5)
                        binding.ivDetailStar1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_half));
                    else if (restaurant.getStarsCount() > 0.5)
                        binding.ivDetailStar1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_filled));
                    if (restaurant.getStarsCount() == 1.5)
                        binding.ivDetailStar2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_half));
                    else if (restaurant.getStarsCount() > 1.5)
                        binding.ivDetailStar2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_filled));
                    if (restaurant.getStarsCount() == 2.5)
                        binding.ivDetailStar3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_half));
                    else if (restaurant.getStarsCount() > 2.5)
                        binding.ivDetailStar3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_filled));
                }

                // link for phone number of the restaurant
                if (restaurant.getPhone() != null && !restaurant.getPhone().isEmpty()) {
                    binding.buttonCall.setEnabled(true);
                    binding.buttonCall.setOnClickListener(v -> {
                        Intent i = new Intent(Intent.ACTION_DIAL);
                        i.setData(Uri.parse("tel:" + restaurant.getPhone()));
                        startActivity(i);
                    });
                }
                else {
                    binding.buttonCall.setEnabled(false);
                }

                // for the user to rate this restaurant
                binding.buttonRate.setOnClickListener(v -> createAlertGrade());

                // link for the restaurant's website
                if (restaurant.getWebsite() != null && !restaurant.getWebsite().isEmpty()) {
                    binding.buttonWebsite.setEnabled(true);
                    binding.buttonWebsite.setOnClickListener(v -> {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(restaurant.getWebsite()));
                        startActivity(i);
                    });
                }
                else {
                    binding.buttonWebsite.setEnabled(false);
                }
            }
        });

        // Set the list of workmates who will lunch in this restaurant and observe changes
        RecyclerView rv = binding.rvListWorkmatesForOneRestaurant;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        DetailRestaurantAdapter adapter = new DetailRestaurantAdapter(this, workmatesInterested);
        rv.setAdapter(adapter);

        vm.getWorkmatesForThisRestaurantLiveData(idRestaurant).observe(this, workmates -> {
            workmatesInterested.clear();
            workmatesInterested.addAll(workmates);
            adapter.notifyDataSetChanged();
        });
    }

    protected void createAlertGrade() {

        @SuppressLint("InflateParams") View viewDialog = LayoutInflater.from(this).inflate(R.layout.alertdialog_rates_layout, null);
        Integer[] icons = {R.drawable.ic_star_empty, R.drawable.ic_star_half, R.drawable.ic_star_filled};
        RatesAdapter adapter = new RatesAdapter(this, getResources().getStringArray(R.array.rates), icons);
        ListView listView = viewDialog.findViewById(R.id.listview_alertgrade);
        listView.setAdapter(adapter);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.give_rate)
                .setSingleChoiceItems(adapter, 0, (dialog1, which) -> {
                    vm.addRate(currentUser.getUid(), restaurantSelected.getId(), which + 1);
                    dialog1.dismiss();
                    Toast.makeText(DetailRestaurantActivity.this, R.string.rate_registered, Toast.LENGTH_LONG).show();
                })
                .create();
        dialog.show();
    }
}