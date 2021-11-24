package com.go4lunch2.ui.detail_restaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.databinding.ActivityDetailRestaurantBinding;

public class DetailRestaurantActivity extends AppCompatActivity {

    private String TAG = "MyLog DetailRestaurantA";
    private ActivityDetailRestaurantBinding binding;
    DetailRestaurantViewModel vm;
    RecyclerView rv;
    //List<Workmate> workmatesInterested = Repository.FAKE_LIST_WORKMATES; // TODO : à remplacer
    DetailRestaurantViewState restaurantSelected;
    static public final String RESTAURANT_SELECTED = "restaurant_selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String idRestaurant = intent.getStringExtra(RESTAURANT_SELECTED);

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(DetailRestaurantViewModel.class);
        vm.getDetailRestaurantLiveData(idRestaurant).observe(this, restaurant -> {
            restaurantSelected = restaurant;

        //TODO : essayer suppression
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (restaurant == null) finish();
        else {
            binding.restaurantName.setText(restaurant.getName());
            binding.restaurantDesc1.setText(restaurant.getType() + "-" + restaurant.getAdress());
//TODO : if user has chosen this restaurant
            binding.fabRestaurantChosen.setColorFilter(this.getResources().getColor(R.color.green_select_fab));
//            try {
//                InputStream ims = this.getAssets().open(restaurant.getImage());
//                binding.restaurantImage.setImageDrawable(Drawable.createFromStream(ims, null));
//                ims.close();
//            } catch (IOException ex) {
//                return;
//            }

            binding.buttonRate.setOnClickListener(v -> createAlertGrade());

            if (restaurant.workmatesInterested!=null) {
                rv = binding.rvListWorkmatesForOneRestaurant;
                rv.setLayoutManager(new LinearLayoutManager(this));
                rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                DetailRestaurantAdapter adapter = new DetailRestaurantAdapter(this, restaurant.workmatesInterested);
                rv.setAdapter(adapter);
            }
        }
        });







    }

    protected void createAlertGrade() {
        View viewDialog = LayoutInflater.from(this).inflate(R.layout.alertdialog_rates_layout, null);
        Integer[] icons = {R.drawable.ic_star_empty, R.drawable.ic_star_half, R.drawable.ic_star_filled};
        RatesAdapter adapter = new RatesAdapter(this, getResources().getStringArray(R.array.rates), icons);
        ListView listView = viewDialog.findViewById(R.id.listview_alertgrade);
        listView.setAdapter(adapter);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.give_rate)
                .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vm.addRate("w1", restaurantSelected.id, which + 1);
                        Log.i(TAG, "onClick: grade given" + which);
                        dialog.dismiss();
                        Toast.makeText(DetailRestaurantActivity.this, "You have chosen" + which, Toast.LENGTH_LONG).show();
                    }
                })
                .create();
        dialog.show();
    }

}